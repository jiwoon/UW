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
		task.setType(type);
		task.setFileName(fileName);
		task.setWindow(1);
		task.setState(0);
		task.setCreateTime(new Date());
		return task.save();
	}
	
	public boolean pass(Task task, Integer id) {
//		task.dao().
		task.setState(1);
		task.keep("id", "type", "file_name", "window", "state", "createtime");
		return task.update();
	}
	
	public boolean start(Task task, Integer id, Integer window) {
		task.setState(2);
		task.keep("id", "type", "file_name", "window", "state", "createtime");
		return task.update();
	}
	
	public boolean cancel(Task task, Integer id) {
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
