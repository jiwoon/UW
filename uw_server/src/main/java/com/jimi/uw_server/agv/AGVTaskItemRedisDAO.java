package com.jimi.uw_server.agv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.jfinal.plugin.redis.Redis;
import com.jimi.uw_server.agv.entity.AGVIOTaskItem;

/**
 * AGV任务条目Redis数据访问对象
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AGVTaskItemRedisDAO {

	/**
	 * 添加任务条目，该方法会把新的任务条目插入到现有的任务列表当中，并把它们按任务id轮流排序<br>
	 * 该方法由任务业务层的开始任务方法调用，请勿在其他地方调用
	 */
	public synchronized static void addTaskItem(List<AGVIOTaskItem> taskItems) {
		//判断如果原先的任务队列为空，则在添加任务条目完成后调用sendIOCmd方法
		boolean callSendIOCmdMethod = false;
		if(Redis.use().llen("til") == 0) {
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
		int emptyQueue = 0;
		while(emptyQueue != groupByTaskIdMap.size()) {
			for (Queue<AGVIOTaskItem> queue : groupByTaskIdMap.values()) {
				AGVIOTaskItem item = queue.poll();
				if(item != null) {
					emptyQueue++;
				}
				items.add(item.toString().getBytes());
			}
		}
		Redis.use().del("til");
		Redis.use().lpush("til", items.toArray());
		if(callSendIOCmdMethod) {
			AGVWebSocket.me.sendIOCmd();
		}
	}
	
	
	/**
	 * 删除指定任务id的条目<br>
	 * 该方法由任务业务层的作废任务方法调用，请勿在其他地方调用
	 */
	public synchronized static void removeTaskItemByTaskId(int taskId) {
		for (int i = 0; i < Redis.use().llen("til"); i++) {
			byte[] item = Redis.use().lindex("til", i);
			if(AGVIOTaskItem.fromString(new String(item)).getTaskId() == taskId){
				Redis.use().lrem("til", 1, item);
				i--;
			}
		}
	}

	
	/**
	 * 删除指定条目<br>
	 * 该方法由AGVWebSocket进行调用，请勿在其他地方调用
	 */
	public synchronized static void removeTaskItem(AGVIOTaskItem taskItem) {
		Redis.use().lrem("til", -1, taskItem.toString().getBytes());
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
	public static List<AGVIOTaskItem> appendTaskItems(List<AGVIOTaskItem> taskItems) {
		List<byte[]> items = Redis.use().lrange("til", 0, -1);
		for (byte[] item : items) {
			taskItems.add(AGVIOTaskItem.fromString(new String(item)));
		}
		return taskItems;
	}

}
