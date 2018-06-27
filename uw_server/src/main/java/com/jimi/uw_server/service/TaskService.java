package com.jimi.uw_server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jimi.uw_server.agv.AGVTaskItemRedisDAO;
import com.jimi.uw_server.agv.entity.AGVIOTaskItem;
import com.jimi.uw_server.model.PackingListItem;
import com.jimi.uw_server.model.Task;
import com.jimi.uw_server.model.Window;

public class TaskService {
	
	private static final String getWindowsSql = "SELECT id FROM window";
	
	private static final String getTaskMaterialIdSql = "SELECT material_type_id FROM packing_list_item WHERE task_id = ?";
	
	public boolean create(Task task, Integer type, String fileName) {
		task.setType(type);
		task.setFileName(fileName);
		task.setWindow(1);
		task.setState(0);
		task.setCreateTime(new Date());
		System.out.println("task: " + task);
		return task.save();
	}
	
	public boolean pass(Task task, Integer id) {
		task.setState(1);
		task.keep("id", "type", "file_name", "window", "state", "createtime");
		return task.update();
	}
	
	public boolean start(Task task, Integer id, Integer window) {
		Window getwindow = Window.dao.findById(window);
		List<PackingListItem> items = PackingListItem.dao.find(getTaskMaterialIdSql, id);
		// 根据套料单、物料类型表生成任务条目
		List<AGVIOTaskItem> taskItems = new ArrayList<>();
		for (PackingListItem item : items) {
			AGVIOTaskItem a = new AGVIOTaskItem(item.getMaterialTypeId(), getwindow.getRow(), getwindow.getCol(), id);
//			AGVIOTaskItem a = new AGVIOTaskItem(1, 2, 3, 4);
			taskItems.add(a);
			System.out.println("taskItems: " + taskItems);
			// 把任务条目均匀插入到队列til中（线程同步方法）
			AGVTaskItemRedisDAO.addTaskItem(taskItems);
			taskItems.clear();
		}
		task.setState(2);
		task.keep("id", "type", "file_name", "window", "state", "createtime");
		return task.update();
	}
	
	public boolean cancel(Task task, Integer id) {
		// 判断任务是否处于进行中状态，若是，则把相关的任务条目从til中剔除（线程同步方法） 并 更新任务状态为作废；
		int state = task.findById(id).getState();
		if (state == 2) {
			System.out.println("taskId: " + id);
			AGVTaskItemRedisDAO.removeTaskItemByTaskId(id);
		}
		// 更新任务状态为作废
		task.setState(4);
		task.keep("id", "type", "file_name", "window", "state", "creattime");
		return task.update();
	}
	
//	public Object check(Integer id) {
//		TaskLog taskLog = new TaskLog();
//		taskLog.findFirst(checkTaskSql, id);
//		return taskLog;
//	}
	
	public List<Window> getWindows(Window window) {
		List<Window> windowId;
		windowId = Window.dao.find(getWindowsSql);
		return windowId;
	}

}
