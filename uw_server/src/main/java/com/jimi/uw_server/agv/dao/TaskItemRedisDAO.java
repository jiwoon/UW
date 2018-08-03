package com.jimi.uw_server.agv.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.jfinal.json.Json;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jimi.uw_server.agv.entity.bo.AGVIOTaskItem;

/**
 * AGV任务条目Redis数据访问对象
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class TaskItemRedisDAO {

	private static Cache cache = Redis.use();
	
	
	/**
	 * 是否已经停止分配任务
	 */
	public synchronized static int isPauseAssign() {
		try {
			return cache.get("pause");
		} catch (NullPointerException e) {
			cache.set("pause", 0);
			return isPauseAssign();
		}
	}
	
	
	/**
	 * 设置停止分配任务标志位
	 */
	public synchronized static void setPauseAssign(int pause) {
		cache.set("pause", pause);
	}
	
	
	/**
	 * 添加任务条目，该方法会把新的任务条目插入到现有的任务列表当中，并把它们按任务id轮流排序<br>
	 */
	public synchronized static void addTaskItem(List<AGVIOTaskItem> taskItems) {
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
					items.add(Json.getJson().toJson(item).getBytes());
				}
			}
			if(emptyQueue == groupByTaskIdMap.size()) {
				break;
			}
		}
		Collections.reverse(items);
		cache.del("til");
		cache.lpush("til", items.toArray());
	}
	
	
	/**
	 * 删除指定任务id的未分配的条目<br>
	 */
	public synchronized static void removeUnAssignedTaskItemByTaskId(int taskId) {
		for (int i = 0; i < cache.llen("til"); i++) {
			byte[] item = cache.lindex("til", i);
			AGVIOTaskItem agvioTaskItem = Json.getJson().parse(new String(item), AGVIOTaskItem.class);
			if(agvioTaskItem.getTaskId().intValue() == taskId && agvioTaskItem.getState().intValue() == 0){
				cache.lrem("til", 1, item);
				i--;
			}
		}
	}
	
	
	/**
	 * 删除指定任务id的条目<br>
	 */
	public static void removeTaskItemByTaskId(int taskId) {
		for (int i = 0; i < cache.llen("til"); i++) {
			byte[] item = cache.lindex("til", i);
			AGVIOTaskItem agvioTaskItem = Json.getJson().parse(new String(item), AGVIOTaskItem.class);
			if(agvioTaskItem.getTaskId().intValue() == taskId){
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
			AGVIOTaskItem agvioTaskItem = Json.getJson().parse(new String(item), AGVIOTaskItem.class);
			if(agvioTaskItem.getId().intValue() == taskItem.getId().intValue()){
				agvioTaskItem.setState(state);
				cache.lset("til", i, Json.getJson().toJson(agvioTaskItem).getBytes());
				break;
			}
		}
	}

	
	/**
	 *  填写指定条目的执行机器
	 */
	public synchronized static void updateTaskItemRobot(AGVIOTaskItem taskItem, int robotid) {
		for (int i = 0; i < cache.llen("til"); i++) {
			byte[] item = cache.lindex("til", i);
			AGVIOTaskItem agvioTaskItem = Json.getJson().parse(new String(item), AGVIOTaskItem.class);
			if(agvioTaskItem.getId().intValue() == taskItem.getId().intValue()){
				agvioTaskItem.setRobotId(robotid);
				cache.lset("til", i, Json.getJson().toJson(agvioTaskItem).getBytes());
				break;
			}
		}
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
			taskItems.add(Json.getJson().parse(new String(item), AGVIOTaskItem.class));
		}
		return taskItems;
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
