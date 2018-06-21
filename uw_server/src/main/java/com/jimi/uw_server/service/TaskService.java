package com.jimi.uw_server.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.jimi.uw_server.material.entity.PackingList;
import com.jimi.uw_server.model.Material;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.model.PackingListItem;
import com.jimi.uw_server.model.Task;
import com.jimi.uw_server.util.ExcelHelper;

public class TaskService {
	
	private static List<PackingList> packingLists;
	
	public boolean create(Task task, Integer type, String fileName) {
		// 根据文件名（excel表格的绝对路径）导入数据
		task.setType(type);
		task.setFileName(fileName);
		task.setWindow(1);
		task.setState(0);
		task.setCreateTime(new Date());

		return task.save();
	}
	
	public void insertPackingList(Task task, PackingListItem packingListItem, MaterialType materialType, Integer type, String fileName) {
		File file = new File(fileName);
		// 获取新任务id
		Task newTaskIdSql = task.findFirst("SELECT MAX(id) as newId FROM task");
		Integer newTaskId = newTaskIdSql.get("newId");
		// 获取新建任务类型
		Task taskTypeSql = task.findFirst("SELECT type FROM task WHERE id = ?", newTaskId);
		Integer taskType = taskTypeSql.get("type");
		
		Material material = new Material();

		try {
			ExcelHelper fileReader = ExcelHelper.from(file);
			try {
				packingLists = fileReader.unfill(PackingList.class, 2);
				for (PackingList packingList : packingLists) {
					
					// 计划出库数量
					Integer planQuantity = Integer.parseInt(packingList.getQuantity());
					
					// 获取该物料的库存数量
					MaterialType checkQuantitySql = materialType.findFirst("SELECT remainder_quantity FROM material WHERE type = ("
							+ "SELECT id FROM material_type WHERE no = ?)", packingList.getNo());
					Integer remainderQuantity = checkQuantitySql.get("remainder_quantity");
					
					if(taskType == 1) {
						// 逐条判断库存是否足够，若是，则插入套料单数据；
						if (remainderQuantity > Integer.parseInt(packingList.getQuantity())) {
							
							System.out.println("该物料库存充足，可以出库！");
							// 添加物料类型id
							MaterialType findNoSql = materialType.findFirst("SELECT id FROM material_type WHERE no = ?", packingList.getNo());
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
							System.out.println("该物料库存不足！");
							packingListItem = new PackingListItem();
							continue;
						}					
					} else {
						// 添加物料类型id
						MaterialType findNoSql = materialType.findFirst("SELECT id FROM material_type WHERE no = ?", packingList.getNo());
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
		
	}
	
	public void updateMaterialQuantity(Material material, Integer taskType, Integer remainderQuantity, Integer planQuantity, Integer materialTypeId) {
		if (taskType == 1) {
			remainderQuantity -= planQuantity;
			material = material.findFirst("SELECT id FROM material WHERE type = (SELECT type FROM material WHERE type = ?)", materialTypeId);
			String materialId = material.get("id");
			material.findById(materialId).set("remainder_quantity", remainderQuantity).update();
		} else if (taskType == 0) {
			remainderQuantity += planQuantity;
			material = material.findFirst("SELECT id FROM material WHERE type = (SELECT type FROM material WHERE type = ?)", materialTypeId);
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
		// 根据套料单、物料类型表生成任务条目
		
		// 把任务条目均匀插入到队列til中（线程同步方法）
		
		
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
