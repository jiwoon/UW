package com.jimi.uw_server.agv.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jimi.uw_server.agv.entity.AGVIOTaskItem;
import com.jimi.uw_server.agv.handle.LSSLHandler;

/**
 * AGV任务条目Redis数据访问对象
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class TaskItemRedisDAO {

	private static Cache cache = Redis.use();
	
	
	/**
	 * 添加任务条目，该方法会把新的任务条目插入到现有的任务列表当中，并把它们按任务id轮流排序<br>
	 */
	public synchronized static void addTaskItem(List<AGVIOTaskItem> taskItems) {
		//判断如果原先的任务队列为空，则在添加任务条目完成后调用sendIOCmd方法
		boolean callSendIOCmdMethod = false;
		if(cache.llen("til") == 0) {
			callSendIOCmdMethod = true;
		}
		appendTaskItems(taskItems);
		Map<Integer, Queue<AGVIOTaskItem>> groupByTaskIdMap = new HashMap<>();
		for (AGVIOTaskItem item : taskItems) {
			Queue<AGVIOTaskItem> queue = groupByTaskIdMap.get(item.getTaskId());
			if(queue == null) {
				queue = new LinkedBlockingQueue<>();
				groupByTaskIdMap.put(item.getTaskId(), queue);
			}
			queue.offer(item);
		}
		List<byte[]> items = new ArrayList<>();
		while(true) {
			int emptyQueue = 0;
			for (Queue<AGVIOTaskItem> queue : groupByTaskIdMap.values()) {
				AGVIOTaskItem item = queue.poll();
				if(item == null) {
					emptyQueue++;
				}else {
					items.add(item.toString().getBytes());
				}
			}
			if(emptyQueue == groupByTaskIdMap.size()) {
				break;
			}
		}
		Collections.reverse(items);
		cache.del("til");
		cache.lpush("til", items.toArray());
		if(callSendIOCmdMethod) {
			LSSLHandler.sendLS();
		}
	}
	
	
	/**
	 * 删除指定任务id的条目，注意：只能删除未分配的条目<br>
	 */
	public synchronized static void removeTaskItemByTaskId(int taskId) {
		for (int i = 0; i < cache.llen("til"); i++) {
			byte[] item = cache.lindex("til", i);
			AGVIOTaskItem agvioTaskItem = AGVIOTaskItem.fromString(new String(item));
			if(agvioTaskItem.getTaskId() == taskId && agvioTaskItem.getState() != 0){
				cache.lrem("til", 1, item);
				i--;
			}
		}
	}
	
	
	/**
	 * 更新任务条目状态<br>
	 */
	public synchronized static void updateTaskItemState(AGVIOTaskItem taskItem, int state) {
		for (int i = 0; i < cache.llen("til"); i++) {
			byte[] item = cache.lindex("til", i);
			AGVIOTaskItem agvioTaskItem = AGVIOTaskItem.fromString(new String(item));
			if(agvioTaskItem.getTaskId() == taskItem.getTaskId() && 
					agvioTaskItem.getMaterialTypeId() == taskItem.getMaterialTypeId()){
				agvioTaskItem.setState(state);
				cache.lset("til", i, agvioTaskItem.toString().getBytes());
				break;
			}
		}
	}

	
	/**
	 * 删除指定条目<br>
	 */
	public synchronized static void removeTaskItem(AGVIOTaskItem taskItem) {
		cache.lrem("til", 1, taskItem.toString().getBytes());
	}
	

	/**
	 * 返回任务条目列表的副本
	 */
	public synchronized static List<AGVIOTaskItem> getTaskItems() {
		List<AGVIOTaskItem> taskItems = new ArrayList<>();
		return appendTaskItems(taskItems);
	}


	/**
	 * 把redis的til内容追加到参数里然后返回
	 */
	public synchronized static List<AGVIOTaskItem> appendTaskItems(List<AGVIOTaskItem> taskItems) {
		List<byte[]> items = cache.lrange("til", 0, -1);
		for (byte[] item : items) {
			taskItems.add(AGVIOTaskItem.fromString(new String(item)));
		}
		return taskItems;
	}

	
	public synchronized static void setLcn(int lcn) {
		cache.set("lcn", lcn);
	}
	
	
	public synchronized static int getLcn() {
		try {
			return cache.get("lcn");
		} catch (NullPointerException e) {
			setLcn(0);
			return getLcn();
		}
	}
	
	
	/**
	 * 获取一个新的CmdId
	 */
	public synchronized static int getCmdId() {
		int cmdid = 0;
		try {
			cmdid = cache.get("cmdid");
		} catch (NullPointerException e) {
		}
		cmdid%=999999;
		cmdid++;
		cache.set("cmdid", cmdid);
		return cmdid;
	}
}
