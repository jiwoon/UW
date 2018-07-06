package com.jimi.uw_server.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jimi.uw_server.agv.dao.AGVTaskItemRedisDAO;
import com.jimi.uw_server.agv.entity.AGVIOTaskItem;
import com.jimi.uw_server.exception.OperationException;
import com.jimi.uw_server.model.Material;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.model.PackingListItem;
import com.jimi.uw_server.model.Task;
import com.jimi.uw_server.model.User;
import com.jimi.uw_server.model.Window;
import com.jimi.uw_server.model.bo.PackingListItemBO;
import com.jimi.uw_server.model.vo.TaskVO;
import com.jimi.uw_server.model.vo.TaskVO2;
import com.jimi.uw_server.service.entity.Page;
import com.jimi.uw_server.util.ErrorLogWritter;
import com.jimi.uw_server.util.ExcelHelper;

public class TaskService {
	
	private static final String getWindowsSql = "SELECT id FROM window";
	
	private static final String getTaskMaterialIdSql = "SELECT material_type_id FROM packing_list_item WHERE task_id = ?";
	
	private static final String taskSql = "SELECT * FROM task limit ?, ?";
	
	private static final String doPaginateSql = "SELECT COUNT(*) as total FROM task";
	
	private static final String getNewTaskIdSql = "SELECT MAX(id) as newId FROM task";
	
	private static final String getTaskTypeSql = "SELECT type FROM task WHERE id = ?";
	
	private static final String getQuantitySql = "SELECT remainder_quantity FROM material WHERE type = ("
			+ "SELECT id FROM material_type WHERE no = ?)";
	
	private static final String getNoSql = "SELECT id FROM material_type WHERE no = ?";
	
	private static final String getMaterialId = "SELECT id FROM material WHERE type = (SELECT type FROM material WHERE type = ?)";
	
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
		List<AGVIOTaskItem> taskItems = new ArrayList<AGVIOTaskItem>();
		for (PackingListItem item : items) {
			AGVIOTaskItem a = new AGVIOTaskItem(item.getMaterialTypeId(), getwindow.getRow(), getwindow.getCol(), id);
			taskItems.add(a);
		}
		// 把任务条目均匀插入到队列til中（线程同步方法）
		AGVTaskItemRedisDAO.addTaskItem(taskItems);
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
		// 如果任务状态为已完成或者已作废，就那么就不能再作废它
//		if (state == 3 || state == 4) {
//			throw new OperationException("该任务已完成或已作废，不能再作废它！");
//		} else if (state == 2) {
//			System.out.println("taskId: " + id);
//			AGVTaskItemRedisDAO.removeTaskItemByTaskId(id);
//		}
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
	
	public Object select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter) {
		List<Task> task;
		List<TaskVO> taskVO2 = new ArrayList<TaskVO>();
		
		Page page = new Page();
		page.setPageSize(pageSize);
		page.setPageNumber(pageNo);
		Integer totallyRow = Integer.parseInt(User.dao.findFirst(doPaginateSql).get("total").toString());
		page.setTotalRow(totallyRow);
		Integer firstIndex = (page.getPageNumber()-1)*page.getPageSize();
		task= Task.dao.find(taskSql, firstIndex, page.getPageSize());
		
		for (Task item : task) {
			TaskVO t = new TaskVO(item.getId(), item.getState(), item.getType(), item.getFileName(), item.getCreateTime());
			taskVO2.add(t);
		}
		
		page.setList(taskVO2);
		
		return page;
	}
	
	//将excel表格的物料相关信息写入套料单数据表
	public static void insertPackingList(Task task, PackingListItem packingListItem, MaterialType materialType, Integer type, String fullFileName) {
		List<PackingListItemBO> item;
			
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
							System.out.println("料号为：" + packingList.getNo() +  "的物料库存充足，可以出库！");
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
							ErrorLogWritter.save("料号为：" + packingList.getNo() +  "的物料库存不足！");
							throw new OperationException("料号为：" + packingList.getNo() +  "的物料库存不足！");
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
				System.out.println("文件" + file.getName() + "删除成功！");
			} else {
				ErrorLogWritter.save("文件" + file.getName() + "删除失败！");
				throw new OperationException("文件" + file.getName() + "删除失败！");
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
