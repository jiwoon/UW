package com.jimi.uw_server.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jimi.uw_server.agv.dao.TaskItemRedisDAO;
import com.jimi.uw_server.agv.entity.bo.AGVIOTaskItem;
import com.jimi.uw_server.exception.OperationException;
import com.jimi.uw_server.model.Material;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.model.PackingListItem;
import com.jimi.uw_server.model.Task;
import com.jimi.uw_server.model.Window;
import com.jimi.uw_server.model.bo.PackingListItemBO;
import com.jimi.uw_server.model.vo.TaskVO;
import com.jimi.uw_server.service.base.SelectService;
import com.jimi.uw_server.service.entity.PagePaginate;
import com.jimi.uw_server.util.ExcelHelper;

/**
 * 任务业务层
 * @author HardyYao
 * @createTime 2018年6月8日
 */
public class TaskService {

	private static SelectService selectService = Enhancer.enhance(SelectService.class);

	private static final String getWindowsSql = "SELECT id FROM window";

	private static final String getTaskMaterialIdSql = "SELECT * FROM packing_list_item WHERE task_id = ?";

	private static final String getNewTaskIdSql = "SELECT MAX(id) as newId FROM task";

	private static final String getTaskTypeSql = "SELECT type FROM task WHERE id = ?";

	private static final String getQuantitySql = "SELECT SUM(remainder_quantity) AS remainderQuantity FROM material WHERE type = ("
			+ "SELECT id FROM material_type WHERE no = ?)";

	private static final String getNoSql = "SELECT id FROM material_type WHERE no = ?";

	private static final String getMaterialIdSql = "SELECT id FROM material WHERE type = (SELECT type FROM material WHERE type = ?)";

	public synchronized boolean createIOTask(Task task, Integer type, String fileName, String fullFileName) throws Exception {
		if (!(fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))) {
			return false;
		}
		List<PackingListItemBO> items;
		File file = new File(fullFileName);
		ExcelHelper fileReader = ExcelHelper.from(file);
		items = fileReader.unfill(PackingListItemBO.class, 2);
		if (items == null) {
			return false;
		} else {
			task.setType(type);
			task.setFileName(fileName);
			task.setState(0);
			task.setCreateTime(new Date());
		}
		return task.save();
	}

	public boolean pass(Task task, Integer id) {
		task.setState(1);
		task.keep("id", "type", "file_name", "window", "state", "createtime");
		return task.update();
	}

	public boolean start(Task task, Integer id, Integer window) {
		List<PackingListItem> items = PackingListItem.dao.find(getTaskMaterialIdSql, id);
		// 根据套料单、物料类型表生成任务条目
		List<AGVIOTaskItem> taskItems = new ArrayList<AGVIOTaskItem>();
		for (PackingListItem item : items) {
			AGVIOTaskItem a = new AGVIOTaskItem(item);
			taskItems.add(a);
		}
		// 把任务条目均匀插入到队列til中
		TaskItemRedisDAO.addTaskItem(taskItems);
		task.setState(2);
		task.keep("id", "type", "file_name", "window", "state", "createtime");
		return task.update();
	}

	public boolean cancel(Task task, Integer id) {
		// 判断任务是否处于进行中状态，若是，则把相关的任务条目从til中剔除（线程同步方法），并更新任务状态为作废；
		int state = task.findById(id).getState();
		if (state == 2) {
			TaskItemRedisDAO.removeTaskItemByTaskId(id);
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

	public Object check(Integer id) {
 		Task task = Task.dao.findById(id);
		Integer type = task.getType();
		Page<Record> result = new Page<Record>();
		if (type == 0 || type == 1) {
			result = selectService.select(new String[] {"task_log", "packing_list_item", "material", "material_type"},
					new String[] {"task_log.task_id = packing_list_item.task_id", "task_log.material_id = material.id", 
							"material_type.id = material.type"}, null, null, null, null, null);
			System.out.println("result: " + result);
		} else if (type == 2) {
			
		} else if (type == 3) {
			
		}
		
		return result;
	}

	public List<Window> getWindows(Window window) {
		List<Window> windowId;
		windowId = Window.dao.find(getWindowsSql);
		return windowId;
	}

	public Object select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter) {
		List<TaskVO> taskVO = new ArrayList<TaskVO>();

		if (filter != null) {
			if (filter.contains("createTimeString")) {
				filter = filter.replace("createTimeString", "create_time");
			}
			if (filter.contains("fileName")) {
				filter = filter.replace("fileName", "file_name");
			}
		}

		Page<Record> result = selectService.select("task", pageNo, pageSize, ascBy, descBy, filter);

		int totallyRow =  result.getTotalRow();
		for (Record res : result.getList()) {
			TaskVO t = new TaskVO(res.get("id"), res.get("state"), res.get("type"), res.get("file_name"), res.get("create_time"));
			taskVO.add(t);
		}

		PagePaginate pagePaginate = new PagePaginate();
		pagePaginate.setPageSize(pageSize);
		pagePaginate.setPageNumber(pageNo);
		pagePaginate.setTotalRow(totallyRow);

		pagePaginate.setList(taskVO);

		return pagePaginate;
	}

	//将excel表格的物料相关信息写入套料单数据表
	public static boolean insertPackingList(PackingListItem packingListItem, Integer type, String fullFileName) throws Exception {
		List<PackingListItemBO> items;

		File file = new File(fullFileName);
		// 获取新任务id
		Task newTaskIdSql = Task.dao.findFirst(getNewTaskIdSql);
		Integer newTaskId = newTaskIdSql.get("newId");
		// 获取新建任务类型：0入库 1出库 2盘点 3位置优化
		Task taskTypeSql = Task.dao.findFirst(getTaskTypeSql, newTaskId);
		Integer taskType = taskTypeSql.getType();

		Material material = new Material();

		// 读取excel表格的套料单数据，将数据一条条写入到套料单表；如果任务类型为出/入库，还需要修改物料实体表中对应物料的库存数量
		ExcelHelper fileReader = ExcelHelper.from(file);

		items = fileReader.unfill(PackingListItemBO.class, 2);
		for (PackingListItemBO item : items) {
			// 计划出库数量
			Integer planQuantity = item.getQuantity();

			Integer remainderQuantity = 0;

			// 获取将要入库/出库的物料的库存数量
			MaterialType checkQuantitySql = MaterialType.dao.findFirst(getQuantitySql, item.getNo());
			if (checkQuantitySql.get("remainderQuantity") == null) {
				continue;
//				throw new OperationException(item.getNo() + "插入套料单失败，可能是物料实体表里面不存在套料单中对应的物料类型！");
			} else {
				remainderQuantity = Integer.parseInt(checkQuantitySql.get("remainderQuantity").toString());
			}

			if(taskType == 1) {
				// 逐条判断库存是否足够，若是，则插入套料单数据
				if (remainderQuantity >= planQuantity) {
				// 添加物料类型id
				MaterialType findNoSql = MaterialType.dao.findFirst(getNoSql, item.getNo());
				Integer materialId = findNoSql.getId();
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
					throw new OperationException("料号为：" + item.getNo() +  "的物料库存不足！");
				}	
			} else {
				// 添加物料类型id
				MaterialType findNoSql = MaterialType.dao.findFirst(getNoSql, item.getNo());
				Integer materialId = findNoSql.getId();
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
		if (file.exists()) {
			if (file.delete()) {
			} else {
				throw new OperationException("文件" + file.getName() + "删除失败！");
			}
		}

		return true;
	}

	//更新物料实体表中的库存数量
	public static void updateMaterialQuantity(Material material, Integer taskType, Integer remainderQuantity, Integer planQuantity, Integer materialTypeId) {
		if (taskType == 1) {
			remainderQuantity -= planQuantity;
			material = material.findFirst(getMaterialIdSql, materialTypeId);
			String materialId = material.getId();
			material.findById(materialId).set("remainder_quantity", remainderQuantity).update();
		} else if (taskType == 0) {
			remainderQuantity += planQuantity;
			material = material.findFirst(getMaterialIdSql, materialTypeId);
			String materialId = material.getId();
			material.findById(materialId).set("remainder_quantity", remainderQuantity).update();
		} else {
			return ;
		}
	}

}