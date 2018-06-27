package com.jimi.uw_server.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.jimi.uw_server.exception.OperationException;
import com.jimi.uw_server.model.Material;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.model.PackingListItem;
import com.jimi.uw_server.model.Task;
import com.jimi.uw_server.model.bo.PackingListItemBO;
import com.jimi.uw_server.util.ErrorLogWritter;
import com.jimi.uw_server.util.ExcelHelper;

//用于将excel表格的物料相关信息写入套料单数据表，并更新物料实体表中的库存数量
public class TaskHelperService {
	
	private static List<PackingListItemBO> item;
	
	private static final String getNewTaskIdSql = "SELECT MAX(id) as newId FROM task";
	
	private static final String getTaskTypeSql = "SELECT type FROM task WHERE id = ?";
	
	private static final String getQuantitySql = "SELECT remainder_quantity FROM material WHERE type = ("
			+ "SELECT id FROM material_type WHERE no = ?)";
	
	private static final String getNoSql = "SELECT id FROM material_type WHERE no = ?";
	
	private static final String getMaterialId = "SELECT id FROM material WHERE type = (SELECT type FROM material WHERE type = ?)";
	
	//将excel表格的物料相关信息写入套料单数据表
	public static void insertPackingList(Task task, PackingListItem packingListItem, MaterialType materialType, Integer type, String fullFileName) {
		File file = new File(fullFileName);
		// 获取新任务id
		Task newTaskIdSql = task.findFirst(getNewTaskIdSql);
		Integer newTaskId = newTaskIdSql.get("newId");
		// 获取新建任务类型：0入库 1出库 2盘点 3位置优化
		Task taskTypeSql = task.findFirst(getTaskTypeSql, newTaskId);
		Integer taskType = taskTypeSql.get("type");
		
		Material material = new Material();
		
		// 读取excel表格的套料单数据，将数据一条条写入到套料单表；如果任务类型为出/入库，还需要修改物料实体表中对应物料的库存数量
		try {
			ExcelHelper fileReader = ExcelHelper.from(file);
			try {
				item = fileReader.unfill(PackingListItemBO.class, 2);
				for (PackingListItemBO packingList : item) {
					
					// 计划出库数量
					Integer planQuantity = Integer.parseInt(packingList.getQuantity());
					
					// 获取将要入库/出库的物料的库存数量
					MaterialType checkQuantitySql = materialType.findFirst(getQuantitySql, packingList.getNo());
					Integer remainderQuantity = checkQuantitySql.get("remainder_quantity");
					
					if(taskType == 1) {
						// 逐条判断库存是否足够，若是，则插入套料单数据；
						if (remainderQuantity >= Integer.parseInt(packingList.getQuantity())) {
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
							ErrorLogWritter.save("该物料库存不足！");
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
				ErrorLogWritter.save("文件删除失败！");
				System.out.println("文件删除失败！");
			}
		}
	}

	//更新物料实体表中的库存数量
	public static void updateMaterialQuantity(Material material, Integer taskType, Integer remainderQuantity, Integer planQuantity, Integer materialTypeId) {
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

}
