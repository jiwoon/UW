package com.jimi.uw_server.agv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.jfinal.plugin.redis.Redis;
import com.jimi.uw_server.agv.entity.AGVIOTaskItem;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.model.Robot;

/**
 * AGV任务条目发送者
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AGVTaskItemSender {

	private static final String ENABLED_ROBOT_SQL = "SELECT * FROM robot WHERE enabled = ?";
	
	private static final String SPECIFIED_ID_MATERIAL_TYPE_SQL = "SELECT * FROM material_type WHERE id IN()";
	
	/**
	 * 指令发送方法<br>该方法由AGVWebSocket进行调用，请勿在其他地方调用
	 */
	public synchronized static void send() {
		//判断til是否为空
		List<AGVIOTaskItem> taskItems = new ArrayList<>();
		setTaskItems(taskItems);
		if(taskItems.isEmpty()) {
			return;
		}
		//统计当前有效robot数目赋值到cn
		int cn = Robot.dao.find(ENABLED_ROBOT_SQL, 1).size();
		int lcn = Redis.use().get("lcn");
		if(lcn > cn - 1) {
			lcn = cn - 1;
			Redis.use().set("lcn", lcn);
			return;
		}
		int b = cn;
		cn = cn - lcn;
		lcn = b - 1;
		Redis.use().set("lcn", lcn);
		int a = 0;
		//根据materialType表生成物料是否在架情况映射msm
		Map<Integer, Boolean> msm = new HashMap<>();
		StringBuffer sb = new StringBuffer(SPECIFIED_ID_MATERIAL_TYPE_SQL);
		for (AGVIOTaskItem item : taskItems) {
			msm.put(item.getMaterialTypeId(), null);
			sb.insert(sb.indexOf(")"), "?,");
		}
		sb.delete(sb.lastIndexOf(","), sb.lastIndexOf(",") + 1);
		List<MaterialType> materialTypes = MaterialType.dao.find(sb.toString(), msm.keySet().toArray());
		for (MaterialType materialType : materialTypes) {
			msm.put(materialType.getId(), materialType.getIsOnShelf());
		}
		//获取第a个元素
		for (int i = 0; i < a; i++) {
			AGVIOTaskItem item = taskItems.get(a);
			if(cn == 0) {
				return;
			}
			//判断是否在架
			if(msm.get(item.getMaterialTypeId()) == true) {
				//标记为不在架
				MaterialType materialType = MaterialType.dao.findById(item.getMaterialTypeId());
				materialType.setIsOnShelf(false);
				materialType.update();
				//发送LS
				AGVCommunicator.pushIOTaskItem(item);
				a = 0;
				cn--;
			}else {
				a++;
				a%=taskItems.size();
			}
		}
	}
	
	
	/**
	 * 添加任务条目，该方法会把新的任务条目插入到现有的任务列表当中，并把它们按任务id轮流排序<br>
	 * 该方法由任务业务层的开始任务方法调用，请勿在其他地方调用
	 */
	public synchronized static void addTaskItem(List<AGVIOTaskItem> taskItems) {
		setTaskItems(taskItems);
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
	}
	
	
	/**
	 * 删除指定任务id的条目<br>
	 * 该方法由任务业务层的作废任务方法调用，请勿在其他地方调用
	 */
	public synchronized static void removeTaskItemByTaskId(int taskId) {
		for (int i = 0; i < Redis.use().llen("til"); i++) {
			String item = Redis.use().lindex("til", i);
			if(AGVIOTaskItem.fromString(item).getTaskId() == taskId){
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
		Redis.use().lrem("til", -1, taskItem.toString());
	}
	

	/**
	 * 返回任务条目列表的副本
	 */
	public synchronized static List<AGVIOTaskItem> getTaskItems() {
		List<AGVIOTaskItem> taskItems = new ArrayList<>();
		return setTaskItems(taskItems);
	}


	private static List<AGVIOTaskItem> setTaskItems(List<AGVIOTaskItem> taskItems) {
		List<byte[]> items = Redis.use().lrange("til", 0, -1);
		for (byte[] item : items) {
			taskItems.add(AGVIOTaskItem.fromString(new String(item)));
		}
		return taskItems;
	}

}
