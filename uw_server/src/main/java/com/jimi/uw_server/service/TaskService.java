package com.jimi.uw_server.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jimi.uw_server.agv.AGVTaskItemRedisDAO;
import com.jimi.uw_server.agv.entity.AGVIOTaskItem;
import com.jimi.uw_server.exception.OperationException;
import com.jimi.uw_server.material.entity.PackingList;
import com.jimi.uw_server.model.Material;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.model.PackingListItem;
import com.jimi.uw_server.model.Task;
import com.jimi.uw_server.model.Window;
import com.jimi.uw_server.util.ExcelHelper;

public class TaskService {
	
	private static List<PackingList> packingLists;
	
	private static final String getWindowsSql = "SELECT id FROM window";
	
	private static final String getNewTaskIdSql = "SELECT MAX(id) as newId FROM task";
	
	private static final String getTaskTypeSql = "SELECT type FROM task WHERE id = ?";
	
	private static final String getQuantitySql = "SELECT remainder_quantity FROM material WHERE type = ("
			+ "SELECT id FROM material_type WHERE no = ?)";
	
	private static final String getNoSql = "SELECT id FROM material_type WHERE no = ?";
	
	private static final String getMaterialId = "SELECT id FROM material WHERE type = (SELECT type FROM material WHERE type = ?)";
	
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
	
	public void insertPackingList(Task task, PackingListItem packingListItem, MaterialType materialType, Integer type, String fullFileName) {
		File file = new File(fullFileName);
		// 获取新任务id
		Task newTaskIdSql = task.findFirst(getNewTaskIdSql);
		Integer newTaskId = newTaskIdSql.get("newId");
		// 获取新建任务类型：0入库 1出库 2盘点 3位置优化
		Task taskTypeSql = task.findFirst(getTaskTypeSql, newTaskId);
		Integer taskType = taskTypeSql.get("type");
		
		Material material = new Material();
		
		try {
			ExcelHelper fileReader = ExcelHelper.from(file);
			try {
				packingLists = fileReader.unfill(PackingList.class, 2);
				for (PackingList packingList : packingLists) {
					
					// 计划出库数量
					Integer planQuantity = Integer.parseInt(packingList.getQuantity());
					
					// 获取将要入库/出库的物料的库存数量
					MaterialType checkQuantitySql = materialType.findFirst(getQuantitySql, packingList.getNo());
					Integer remainderQuantity = checkQuantitySql.get("remainder_quantity");
					
					if(taskType == 1) {
						// 逐条判断库存是否足够，若是，则插入套料单数据；
						if (remainderQuantity > Integer.parseInt(packingList.getQuantity())) {
							System.out.println("该物料库存充足，可以出库！");
							// 添加物料类型id
							MaterialType findNoSql = materialType.findFirst(getNoSql, packingList.getNo());
							Integer materialId = findNoSql.get("id");
							packingListItem.setMaterialTypeId(materialId);
							// 添加计划出入库数量
							packingListItem.setQuantity(planQuantity);
							// 添加任务id
							packingListItem.setTaskId(newTaskId);
							// 保存该记录到套料单表
							packingListItem.save();
							
							// 更新物料数量
							updateMaterialQuantity(material, taskType, remainderQuantity, planQuantity, materialId);
							
							// new一个PackingListItem，否则前面的记录会被覆盖掉
							packingListItem = new PackingListItem();
						} else {	// 否则，提示库存不足
							packingListItem = new PackingListItem();
							throw new OperationException("该物料库存不足！");
						}					
					} else {
						// 添加物料类型id
						MaterialType findNoSql = materialType.findFirst(getNoSql, packingList.getNo());
						Integer materialId = findNoSql.get("id");
						packingListItem.setMaterialTypeId(materialId);
						// 添加计划出入库数量
						packingListItem.setQuantity(planQuantity);
						// 添加任务id
						packingListItem.setTaskId(newTaskId);
						// 保存该记录到套料单表
						packingListItem.save();
						
						// 更新物料数量
						updateMaterialQuantity(material, taskType, remainderQuantity, planQuantity, materialId);
						
						// new一个PackingListItem，否则前面的记录会被覆盖掉
						packingListItem = new PackingListItem();
						}
						
					}
					
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (file.exists()) {
			if (file.delete()) {
				System.out.println("文件删除成功！");
			} else {
				System.out.println("文件删除失败！");
			}
		} 
	}
	
	public void updateMaterialQuantity(Material material, Integer taskType, Integer remainderQuantity, Integer planQuantity, Integer materialTypeId) {
		if (taskType == 1) {
			remainderQuantity -= planQuantity;
			System.out.println("remainderQuantity: " + remainderQuantity);
			material = material.findFirst(getMaterialId, materialTypeId);
			String materialId = material.get("id");
			material.findById(materialId).set("remainder_quantity", remainderQuantity).update();
		} else if (taskType == 0) {
			remainderQuantity += planQuantity;
			System.out.println("remainderQuantity: " + remainderQuantity);
			material = material.findFirst(getMaterialId, materialTypeId);
			String materialId = material.get("id");
			material.findById(materialId).set("remainder_quantity", remainderQuantity).update();
		} else {
			return ;
		}
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
