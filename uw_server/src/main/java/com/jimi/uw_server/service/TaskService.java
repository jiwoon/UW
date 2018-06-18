package com.jimi.uw_server.service;

import java.util.Date;

import com.jimi.uw_server.model.Task;

public class TaskService {
	
//	private static String checkTaskSelectSql = "SELECT material_type.no as materialNo, packing_list_item.quantity as requestQuantity,"
//			+ "task_log.quantity as actualQuantity, task_log.time as finishTime";
//	
//	private static String checkTaskNonSelectSql = "FROM material_type,packing_list_item,task_log "
//			+ "where task_log.id = ? and material_type.id = packing_list_item.material_type_id"
//			+ "and packing_list_item.task_id = task_log.task_id";
	
//	private static String checkTaskSql = 
//			"SELECT material_type.no as materialNo, packing_list_item.quantity as requestQuantity,"
//	+ "task_log.quantity as actualQuantity, task_log.time as finishTime "
//	+ "FROM material_type,packing_list_item,task_log "
//	+ "WHERE task_log.task_id = ? AND task_log.task_id = packing_list_item.task_id" 
//	+ "AND packing_list_item.material_type_id = material_type.id";
	
	public boolean create(Task task, Integer type, String fileName) {
		// 逐条判断库存是否足够，若是，则插入套料单数据；否则，提示库存不足
		
		
		task.setType(type);
		task.setFileName(fileName);
		task.setWindow(1);
		task.setState(0);
		task.setCreateTime(new Date());
		return task.save();
	}
	
	public boolean pass(Task task, Integer id) {
		task.setState(1);
		task.keep("id", "type", "file_name", "window", "state", "createtime");
		return task.update();
	}
	
	public boolean start(Task task, Integer id, Integer window) {
		// 根据套料单、物料类型表生成任务条目
		
		//把任务条目均匀插入到队列til中（线程同步方法）
		
		
		task.setState(2);
		task.keep("id", "type", "file_name", "window", "state", "createtime");
		return task.update();
	}
	
	public boolean cancel(Task task, Integer id) {
		// 判断任务是否处于进行中状态，若是，则把相关的任务条目从til中剔除（线程同步方法） 并 更新任务状态为作废；否则，更新任务状态为作废
		// 任务条目til存放在redis中
		
		
		task.setState(4);
		task.keep("id", "type", "file_name", "window", "state", "creattime");
		return task.update();
	}
	
//	public Object check(Integer id) {
//		TaskLog taskLog = new TaskLog();
//		taskLog.findFirst(checkTaskSql, id);
//		return taskLog;
//	}

}
