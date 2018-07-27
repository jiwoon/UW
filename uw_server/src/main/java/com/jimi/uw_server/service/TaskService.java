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
import com.jimi.uw_server.model.TaskLog;
import com.jimi.uw_server.model.User;
import com.jimi.uw_server.model.Window;
import com.jimi.uw_server.model.bo.PackingListItemBO;
import com.jimi.uw_server.model.vo.IOTaskDetailVO;
import com.jimi.uw_server.model.vo.TaskVO;
import com.jimi.uw_server.model.vo.WindowTaskItemsVO;
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

	private static final String getNoSql = "SELECT id FROM material_type WHERE no = ?";
	
	private static final String getItemdetails = "SELECT task_log.material_id as materialId, task_log.quantity FROM task_log, packing_list_item, "
			+ "material_type WHERE task_log.task_id = ? AND packing_list_item.task_id = ? AND "
			+ "packing_list_item.material_type_id = material_type.id AND material_type.no = ?";
	
	private static final String getTaskInProcessSql = "SELECT id FROM task WHERE state = 2 AND window = ?";
	
	private static final String getMaterialTypeSql = "SELECT material.type FROM material, material_type WHERE material_type.no = ?";


	public boolean createIOTask(Task task, Integer type, String fileName, String fullFileName) throws Exception {
		// 如果文件格式不对，则返回false，提示检查文件格式及内容格式
		if (!(fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))) {
			return false;
		}
		List<PackingListItemBO> items;
		File file = new File(fullFileName);
		ExcelHelper fileReader = ExcelHelper.from(file);
		items = fileReader.unfill(PackingListItemBO.class, 2);
		// 如果套料单表头不对，则返回false，提示检查文件格式及内容格式
		if (items == null) {
			return false;
		} else {	// 如果套料单格式正确，则创建一条新的任务记录
			task.setType(type);
			task.setFileName(fileName);
			task.setState(0);
			task.setCreateTime(new Date());
		}
		return task.save();
	}


	public boolean pass(Integer id) {
		Task task = Task.dao.findById(id);
		task.setState(1);
		return task.update();
	}


	public boolean start(Integer id, Integer window) {
		Task task = Task.dao.findById(id);
		task.setWindow(window);
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
		return task.update();
	}


	public boolean cancel(Integer id) {
		Task task = Task.dao.findById(id);
		// 判断任务是否处于进行中状态，若是，则把相关的任务条目从til中剔除（线程同步方法），并更新任务状态为作废；
		int state = task.getState();
		if (state == 2) {
			TaskItemRedisDAO.removeTaskItemByTaskId(id);
		}
		task.setState(4);
		return task.update();
	}


	// 查看任务详情
	public Object check(Integer id, Integer pageSize, Integer pageNo) {
 		Task task = Task.dao.findById(id);
		Integer type = task.getType();
		// 如果任务类型为出入库
		if (type == 0 || type == 1) {
			List<IOTaskDetailVO> ioTaskDetailVO = new ArrayList<IOTaskDetailVO>();

			// 先进行多表查询，查询出同一个任务id的套料单表的id,物料类型表的料号no,套料单表的计划出入库数量quantity,套料单表对应任务的实际完成时间finish_time
//			List<PackingListItem> packingList = PackingListItem.dao.find(getPackingListItemSql, id);
			Page<Record> packingListItems = selectService.select(new String[] {"packing_list_item", "material_type"}, 
					new String[] {"packing_list_item.task_id = " + id.toString(), "material_type.id = packing_list_item.material_type_id"}, 
					pageNo, pageSize, null, null, null);

			// 获取查询记录总行数
			int totallyRow =  packingListItems.getTotalRow();

			// 遍历同一个任务id的套料单数据
			for (Record packingListItem : packingListItems.getList()) {
				// 查询task_log中的material_id,quantity
				// 这里在for循环中执行了sql查询，会影响执行效率，暂时还没想到两全其美的解决方案，争取这周(7.23-7.28)想出解决方案
				List<TaskLog> taskLog = TaskLog.dao.find(getItemdetails, id, id, packingListItem.get("MaterialType_No"));
				Integer actualQuantity = 0;
				// 实际出入库数量要根据task_log中的出入库数量记录进行累加得到
				for (TaskLog tl : taskLog) {
					actualQuantity += tl.getQuantity();
				}
				IOTaskDetailVO io = new IOTaskDetailVO(packingListItem.get("PackingListItem_Id"), packingListItem.get("MaterialType_No"), packingListItem.get("PackingListItem_Quantity"), 
						actualQuantity, packingListItem.get("PackingListItem_FinishTime"));
				io.setDetails(taskLog);
				ioTaskDetailVO.add(io);
			}

			// 分页，设置页码，每页显示条目等
			PagePaginate pagePaginate = new PagePaginate();
			pagePaginate.setPageSize(pageSize);
			pagePaginate.setPageNumber(pageNo);
			pagePaginate.setTotalRow(totallyRow);

			pagePaginate.setList(ioTaskDetailVO);

			return pagePaginate;
		} else if (type == 2) {		//如果任务类型为盘点
			return null;
		} else if (type == 3) {		//如果任务类型为位置优化
			return null;
		}
		return null;
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

		// 获取查询记录总行数
		int totallyRow =  result.getTotalRow();
		for (Record res : result.getList()) {
			TaskVO t = new TaskVO(res.get("id"), res.get("state"), res.get("type"), res.get("file_name"), res.get("create_time"));
			taskVO.add(t);
		}

		// 分页，设置页码，每页显示条目等
		PagePaginate pagePaginate = new PagePaginate();
		pagePaginate.setPageSize(pageSize);
		pagePaginate.setPageNumber(pageNo);
		pagePaginate.setTotalRow(totallyRow);

		pagePaginate.setList(taskVO);

		return pagePaginate;
	}
	
	
	public Object getWindowTaskItems(Integer id, Integer pageNo, Integer pageSize) {
		List<Task> task = Task.dao.find(getTaskInProcessSql, id);
		List<WindowTaskItemsVO> windowTaskItemsVO = new ArrayList<WindowTaskItemsVO>();
		
		PagePaginate pagePaginate = new PagePaginate();
		int totallyRow = 0;
		for (Task t : task) {
			Integer taskId = t.getId();
			
			// 先进行多表查询，查询出仓口id绑定的正在执行中的任务的套料单表的id,套料单文件名，物料类型表的料号no,套料单表的计划出入库数量quantity,套料单表对应任务的实际完成时间finish_time
			Page<Record> windowTaskItems = selectService.select(new String[] {"packing_list_item", "material_type", "task"}, 
					new String[] {"packing_list_item.task_id = " + taskId.toString(), "material_type.id = packing_list_item.material_type_id", 
							"task.id = " + taskId.toString()}, pageNo, pageSize, null, null, null);
			
			// 记录获取查询记录总行数
			totallyRow += windowTaskItems.getTotalRow();

			// 遍历同一个任务id的套料单数据
			for (Record windowTaskItem : windowTaskItems.getList()) {
				// 查询task_log中的material_id,quantity
				// 这里在for循环中执行了sql查询，会影响执行效率，暂时还没想到两全其美的解决方案，争取这周(7.23-7.28)想出解决方案
				List<TaskLog> taskLog = TaskLog.dao.find(getItemdetails, taskId.toString(), taskId.toString(), windowTaskItem.get("MaterialType_No"));
				Integer actualQuantity = 0;
				// 实际出入库数量要根据task_log中的出入库数量记录进行累加得到
				for (TaskLog tl : taskLog) {
					actualQuantity += tl.getQuantity();
				}
				WindowTaskItemsVO wt = new WindowTaskItemsVO(windowTaskItem.get("PackingListItem_Id"), windowTaskItem.get("Task_FileName"), 
						windowTaskItem.get("MaterialType_No"), windowTaskItem.get("PackingListItem_Quantity"), 
						actualQuantity, windowTaskItem.get("PackingListItem_FinishTime"));
				wt.setDetails(taskLog);
				windowTaskItemsVO.add(wt);
			}
	
		}
		// 分页，设置页码，每页显示条目等
		pagePaginate.setPageSize(pageSize);
		pagePaginate.setPageNumber(pageNo);
		pagePaginate.setTotalRow(totallyRow);

		pagePaginate.setList(windowTaskItemsVO);
		
		return pagePaginate;
	}


	//将excel表格的物料相关信息写入套料单数据表
	public boolean insertPackingList(PackingListItem packingListItem, Integer type, String fullFileName) throws Exception {
		List<PackingListItemBO> items;

		File file = new File(fullFileName);
		// 获取新任务id
		Task newTaskIdSql = Task.dao.findFirst(getNewTaskIdSql);
		Integer newTaskId = newTaskIdSql.get("newId");

		// 读取excel表格的套料单数据，将数据一条条写入到套料单表；如果任务类型为出/入库，还需要修改物料实体表中对应物料的库存数量
		ExcelHelper fileReader = ExcelHelper.from(file);

		items = fileReader.unfill(PackingListItemBO.class, 2);
		for (PackingListItemBO item : items) {
			// 计划出库数量
			Integer planQuantity = item.getQuantity();

			MaterialType findNoSql = MaterialType.dao.findFirst(getNoSql, item.getNo());
			// 判断物料类型表中是否存在对应的料号，若不存在，应将对应的任务记录作废掉，并提示操作员检查套料单、新增对应的物料类型
			if (findNoSql == null) {
				Task task = Task.dao.findById(newTaskId);
				task.setState(4);
				task.update();
				return false;
			}
			
			// 若物料类型表中存在对应的料号，不论物料实体表中是否有库存，都允许插入套料单；若库存不足，则在「物料出入库」那里进行处理
			findNoSql = MaterialType.dao.findFirst(getNoSql, item.getNo());
			Integer materialTypeId = findNoSql.getId();
			packingListItem.setMaterialTypeId(materialTypeId);
			// 添加计划出入库数量
			packingListItem.setQuantity(planQuantity);
//			// 添加任务id
			packingListItem.setTaskId(newTaskId);
//			// 保存该记录到套料单表
			packingListItem.save();
			
			// new一个PackingListItem，否则前面的记录会被覆盖掉
			packingListItem = new PackingListItem();

			}
		if (file.exists()) {
			if (file.delete()) {
			} else {
				throw new OperationException("文件" + file.getName() + "删除失败！");
			}
		}

		return true;
	}
	
	
	public void finish(Integer taskId) {
		Task task = new Task();
		task.setId(taskId);
		task.setState(3);
		task.update();
	}


	public void finishItem(Integer packingListItemid) {
		PackingListItem packingListItem = new PackingListItem();
		packingListItem.setId(packingListItemid);
		packingListItem.setFinishTime(new Date());
		packingListItem.update();
	}


	public boolean io(Integer packListItemId, String materialId, Integer quantity, String no, User user) {
		Material material = new Material();
		TaskLog taskLog = new TaskLog();
		
		// 根据套料单id，获取对应的任务id
		PackingListItem packingListItem = PackingListItem.dao.findById(packListItemId);
		
		/*
		 *  新增或减少物料表记录
		 */
		// 判断是出库还是入库
		Task task = Task.dao.findById(packingListItem.getTaskId());
		Integer type = task.getType();
		if (type == 0) {	//如果是入库，则新增一条记录
			material.setId(materialId);
			// 根据料号获取物料类型
			Material getType = Material.dao.findFirst(getMaterialTypeSql, no);
			material.setType(getType.getType());
			material.setRow(0);
			material.setCol(0);
			material.setRemainderQuantity(quantity);
			material.save();
		} else if (type == 1) {	// 如果是出库，则删除对应的物料实体表记录
			material.deleteById(materialId);
		}
		
		// 写入一条出入库任务日志
		taskLog.setTaskId(packingListItem.getTaskId());

		taskLog.setMaterialId(materialId);
		taskLog.setQuantity(quantity);
		// 写入当前使用系统用户的Uid
		taskLog.setOperator(user.getUid());
		// 区分出入库操作人工还是机器操作,暂时先统一写成机器操作
		taskLog.setAuto(true);
		taskLog.setTime(new Date());
		return taskLog.save();
	}
	
	
}