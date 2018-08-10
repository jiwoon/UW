package com.jimi.uw_server.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.json.Json;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
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
import com.jimi.uw_server.model.vo.WindowParkingListItemVO;
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
	
	private static Cache cache = Redis.use();

	private static SelectService selectService = Enhancer.enhance(SelectService.class);
	
	private static final Object LOCK = new Object();

	private static final String GET_NEW_TASK_ID_SQL = "SELECT MAX(id) as newId FROM task";

	private static final String GET_Material_NO_SQL = "SELECT * FROM material_type WHERE no = ?";

	private static final String DELETE_PACKING_LIST_ITEM_SQL = "DELETE FROM packing_list_item WHERE task_id = ?";
	
	private static final String GET_MATERIAL_TYPE_ID_SQL = "SELECT material_type_id FROM packing_list_item WHERE material_type_id = ? AND task_id = ?";

	private static final String GET_TASK_ITEMS_SQL = "SELECT * FROM packing_list_item WHERE task_id = ?";

	private static final String GET_TASK_ITEM_DETAILS_SQL = "SELECT material_id as materialId, quantity FROM task_log WHERE task_id = ? AND material_id In"
			+ "(SELECT id FROM material WHERE type = (SELECT id FROM material_type WHERE no = ?))";

	private static final String GET_WINDOWS_SQL = "SELECT id FROM window";

	private static final String GET_TASK_IN_REDIS_SQL = "SELECT * FROM task WHERE id = ?";

	private static final String UNIQUE_MATERIAL_ID_IN_SAME_TASK_CHECK_SQL = "SELECT * FROM task_log WHERE material_id = ? AND task_id = ?";

	private static final String UNIQUE_MATERIAL_ID_CHECK_SQL = "SELECT * FROM material WHERE id = ?";


	public String createIOTask(Integer type, String fileName, String fullFileName) throws Exception {
		String resultString = "添加成功！";
		
		// 如果文件格式不对，则返回false，提示检查文件格式及内容格式
		if (!(fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))) {
			resultString = "创建任务失败，请检查套料单的文件格式是否正确！";
			return resultString;
		}
		File file = new File(fullFileName);
		ExcelHelper fileReader = ExcelHelper.from(file);
		List<PackingListItemBO> items = fileReader.unfill(PackingListItemBO.class, 2);
		// 如果套料单表头不对，则返回false，提示检查文件格式及内容格式
		if (items == null) {
			resultString = "创建任务失败，请检查套料单的内容格式是否正确！";
			return resultString;
		} else {
			// 插入套料单那里使用了「SELECT MAX(id) FROM task....」,因此这里要加同步锁，不然会有线程安全问题
			synchronized(LOCK) {
				// 如果套料单格式正确，则创建一条新的任务记录
				Task task = new Task();
				task.setType(type);
				task.setFileName(fileName);
				task.setState(0);
				task.setCreateTime(new Date());
				task.save();

				// 读取excel表格的套料单数据，将数据一条条写入到套料单表；如果任务类型为出/入库，还需要修改物料实体表中对应物料的库存数量
				for (PackingListItemBO item : items) {
					// 获取新任务id
					Task newTaskIdDao = Task.dao.findFirst(GET_NEW_TASK_ID_SQL);
					Integer newTaskId = newTaskIdDao.get("newId");
					// 根据料号找到对应的物料类型id
					MaterialType noDao = MaterialType.dao.findFirst(GET_Material_NO_SQL, item.getNo());
					// 判断物料类型表中是否存在对应的料号，若不存在，则将对应的任务记录删除掉，并提示操作员检查套料单、新增对应的物料类型
					if (noDao == null) {
						Db.update(DELETE_PACKING_LIST_ITEM_SQL, newTaskId);
						Task.dao.deleteById(newTaskId);
						resultString = "插入套料单失败，料号为" + item.getNo() + "的物料没有记录在物料类型表中！";
						return resultString;
					}
					// 判断物料是否已被禁用，若已被禁用，则将对应的任务记录删除掉，并提示操作员检查套料单
					if (!noDao.getEnabled()) {
						Db.update(DELETE_PACKING_LIST_ITEM_SQL, newTaskId);
						Task.dao.deleteById(newTaskId);
						resultString = "插入套料单失败，料号为" + item.getNo() + "的物料已被禁用！";
						return resultString;
					}
					// 判断套料单中是否存在相同的料号
					MaterialType materialTypeIdDao = MaterialType.dao.findFirst(GET_MATERIAL_TYPE_ID_SQL, noDao.getId(), newTaskId);
					if (materialTypeIdDao != null) {
						Db.update(DELETE_PACKING_LIST_ITEM_SQL, newTaskId);
						Task.dao.deleteById(newTaskId);
						resultString = "插入套料单失败，料号为" + item.getNo() + "的物料在套料单中重复出现！";
						return resultString;
					}

					
					PackingListItem packingListItem = new PackingListItem();

					// 若物料类型表中存在对应的料号，且该物料未被禁用，不论物料实体表中是否有库存，都允许插入套料单；若库存不足，则在「物料出入库」那里进行处理
					Integer materialTypeId = noDao.getId();
					// 添加物料类型id
					packingListItem.setMaterialTypeId(materialTypeId);
					// 获取计划出库数量
					Integer planQuantity = item.getQuantity();
					// 添加计划出入库数量
					packingListItem.setQuantity(planQuantity);
					// 添加任务id
					packingListItem.setTaskId(newTaskId);
					// 保存该记录到套料单表
					packingListItem.save();
				}

				if (file.exists()) {
		
					if (!file.delete()) {
						throw new OperationException("文件" + file.getName() + "删除失败！");
					}
		
				}
				
				}
			}

		return resultString;
	}


	public boolean pass(Integer id) {
		Task task = Task.dao.findById(id);
		task.setState(1);
		return task.update();
	}


	public boolean start(Integer id, Integer window) {
		Task task = Task.dao.findById(id);
		task.setWindow(window);
		// 根据套料单、物料类型表生成任务条目
		List<AGVIOTaskItem> taskItems = new ArrayList<AGVIOTaskItem>();
		List<PackingListItem> items = PackingListItem.dao.find(GET_TASK_ITEMS_SQL, id);
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
			TaskItemRedisDAO.removeUnAssignedTaskItemByTaskId(id);
		}
		task.setState(4);
		return task.update();
	}


	public Object check(Integer id, Integer pageSize, Integer pageNo) {
 		Task task = Task.dao.findById(id);
		Integer type = task.getType();
		List<IOTaskDetailVO> ioTaskDetailVOs = new ArrayList<IOTaskDetailVO>();
		// 如果任务类型为出入库
		if (type == 0 || type == 1) {
			// 先进行多表查询，查询出同一个任务id的套料单表的id,物料类型表的料号no,套料单表的计划出入库数量quantity,套料单表对应任务的实际完成时间finish_time
			Page<Record> packingListItems = selectService.select(new String[] {"packing_list_item", "material_type"}, 
					new String[] {"packing_list_item.task_id = " + id.toString(), "material_type.id = packing_list_item.material_type_id"}, 
					pageNo, pageSize, null, null, null);
			
			// 遍历同一个任务id的套料单数据
			for (Record packingListItem : packingListItems.getList()) {
				// 查询task_log中的material_id,quantity
				List<TaskLog> taskLog = TaskLog.dao.find(GET_TASK_ITEM_DETAILS_SQL, id, packingListItem.get("MaterialType_No"));
				Integer actualQuantity = 0;
				// 实际出入库数量要根据task_log中的出入库数量记录进行累加得到
				for (TaskLog tl : taskLog) {
					actualQuantity += tl.getQuantity();
				}
				IOTaskDetailVO io = new IOTaskDetailVO(packingListItem.get("PackingListItem_Id"), packingListItem.get("MaterialType_No"), packingListItem.get("PackingListItem_Quantity"), 
						actualQuantity, packingListItem.get("PackingListItem_FinishTime"));
				io.setDetails(taskLog);
				ioTaskDetailVOs.add(io);
			}

			// 分页，设置页码，每页显示条目等
			PagePaginate pagePaginate = new PagePaginate();
			pagePaginate.setPageSize(pageSize);
			pagePaginate.setPageNumber(pageNo);
			pagePaginate.setTotalRow(packingListItems.getTotalRow());
			pagePaginate.setList(ioTaskDetailVOs);

			return pagePaginate;
		}

		else if (type == 2) {		//如果任务类型为盘点
			return null;
		}

		else if (type == 3) {		//如果任务类型为位置优化
			return null;
		}

		return null;
	}


	public List<Window> getWindows() {
		List<Window> windowIds;
		windowIds = Window.dao.find(GET_WINDOWS_SQL);
		return windowIds;
	}


	public Object select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter) {
		Page<Record> result = selectService.select("task", pageNo, pageSize, ascBy, descBy, filter);

		List<TaskVO> taskVOs = new ArrayList<TaskVO>();
		for (Record res : result.getList()) {
			TaskVO t = new TaskVO(res.get("id"), res.get("state"), res.get("type"), res.get("file_name"), res.get("create_time"));
			taskVOs.add(t);
		}

		// 分页，设置页码，每页显示条目等
		PagePaginate pagePaginate = new PagePaginate();
		pagePaginate.setPageSize(pageSize);
		pagePaginate.setPageNumber(pageNo);
		pagePaginate.setTotalRow(result.getTotalRow());
		pagePaginate.setList(taskVOs);

		return pagePaginate;
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

	
	public Object getWindowParkingItem(Integer id) {
		for (int i = 0; i < cache.llen("til"); i++) {
			byte[] item = cache.lindex("til", i);
			AGVIOTaskItem agvioTaskItem = Json.getJson().parse(new String(item), AGVIOTaskItem.class);
			if(agvioTaskItem.getState().intValue() == 2) {
				Task task = Task.dao.findFirst(GET_TASK_IN_REDIS_SQL, agvioTaskItem.getTaskId());
				if (task.getWindow() == id) {
					Integer packingListItemId = agvioTaskItem.getId();

					// 先进行多表查询，查询出仓口id绑定的正在执行中的任务的套料单表的id,套料单文件名，物料类型表的料号no,套料单表的计划出入库数量quantity
					Page<Record> windowParkingListItems = selectService.select(new String[] {"packing_list_item", "material_type", }, 
							new String[] {"packing_list_item.id = " + packingListItemId, "material_type.id = packing_list_item.material_type_id", },
							null, null, null, null, null);

					for (Record windowParkingListItem : windowParkingListItems.getList()) {
						// 查询task_log中的material_id,quantity
						List<TaskLog> taskLogs = TaskLog.dao.find(GET_TASK_ITEM_DETAILS_SQL, task.getId(), windowParkingListItem.get("MaterialType_No"));
						Integer actualQuantity = 0;
						// 实际出入库数量要根据task_log中的出入库数量记录进行累加得到
						for (TaskLog tl : taskLogs) {
							actualQuantity += tl.getQuantity();
						}
						WindowParkingListItemVO wp = new WindowParkingListItemVO(windowParkingListItem.get("PackingListItem_Id"), task.getFileName(), 
								task.getType(), windowParkingListItem.get("MaterialType_No"), windowParkingListItem.get("PackingListItem_Quantity"), 
								actualQuantity);
						wp.setDetails(taskLogs);
						return wp;
					}
				}
			}
		}

		return null;
	}

	public boolean io(Integer packListItemId, String materialId, Integer quantity, User user) {
		// 根据套料单id，获取对应的任务记录
		PackingListItem packingListItem = PackingListItem.dao.findById(packListItemId);
		Task task = Task.dao.findById(packingListItem.getTaskId());
		// 若在同一个出入库任务中重复扫同一个料盘时间戳，则抛出OperationException，错误代码为412
		if (TaskLog.dao.find(UNIQUE_MATERIAL_ID_IN_SAME_TASK_CHECK_SQL, materialId, task.getId()).size() != 0) {
			throw new OperationException("时间戳为" + materialId + "的料盘已在同一个任务中被扫描过，请勿在同一个出入库任务中重复扫描同一个料盘！");
		} else if(Material.dao.find(UNIQUE_MATERIAL_ID_CHECK_SQL, materialId).size() != 0) {
			throw new OperationException("时间戳为" + materialId + "的料盘已入过库，请勿重复入库！");
		}
		/*
		 *  新增或减少物料表记录
		 */
		Integer type = task.getType();		// 获取任务条目对应的任务类型
		if (type == 0) {	//如果是入库，则新增一条记录
			Material material = new Material();
			material.setId(materialId);
			material.setType(packingListItem.getMaterialTypeId());
			material.setRow(0);
			material.setCol(0);
			material.setRemainderQuantity(quantity);
			material.save();
		} else if (type == 1) {		// 如果是出库，则将对应的物料实体表记录置为无效
			Material oldMaterial = Material.dao.findById(materialId);
			oldMaterial.setRow(-1);
			oldMaterial.setCol(-1);
			oldMaterial.setRemainderQuantity(0);
			oldMaterial.update();
		}

		TaskLog taskLog = new TaskLog();
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


	public Object getWindowTaskItems(Integer id, Integer pageNo, Integer pageSize) {
		List<WindowTaskItemsVO> windowTaskItemsVOs = new ArrayList<WindowTaskItemsVO>();
		PagePaginate pagePaginate = new PagePaginate();
		int totallyRow = 0;
		for (int i = 0; i < cache.llen("til"); i++) {
			byte[] item = cache.lindex("til", i);
			AGVIOTaskItem agvioTaskItem = Json.getJson().parse(new String(item), AGVIOTaskItem.class);
			Task task = Task.dao.findFirst(GET_TASK_IN_REDIS_SQL, agvioTaskItem.getTaskId());
			if (task.getWindow() == id) {
				Integer packingListItemId = agvioTaskItem.getId();
					
				// 先进行多表查询，查询出仓口id绑定的正在执行中的任务的套料单表的id,套料单文件名，物料类型表的料号no,套料单表的计划出入库数量quantity
				Page<Record> windowTaskItems = selectService.select(new String[] {"packing_list_item", "material_type", }, 
						new String[] {"packing_list_item.id = " + packingListItemId, "material_type.id = packing_list_item.material_type_id", },
						pageNo, pageSize, null, null, null);
				
				// 记录获取查询记录总行数
				totallyRow += windowTaskItems.getTotalRow();

				for (Record windowTaskItem : windowTaskItems.getList()) {
					// 查询task_log中的material_id,quantity
					List<TaskLog> taskLogs = TaskLog.dao.find(GET_TASK_ITEM_DETAILS_SQL, task.getId(), windowTaskItem.get("MaterialType_No"));
					Integer actualQuantity = 0;
					// 实际出入库数量要根据task_log中的出入库数量记录进行累加得到
					for (TaskLog tl : taskLogs) {
						actualQuantity += tl.getQuantity();
					}
					WindowTaskItemsVO wt = new WindowTaskItemsVO(windowTaskItem.get("PackingListItem_Id"), task.getFileName(), 
							task.getType(), windowTaskItem.get("MaterialType_No"), windowTaskItem.get("PackingListItem_Quantity"), 
							actualQuantity, windowTaskItem.get("PackingListItem_FinishTime"));
					wt.setDetails(taskLogs);
					windowTaskItemsVOs.add(wt);
				}	
			}
		}
		// 分页，设置页码，每页显示条目等
		pagePaginate.setPageSize(pageSize);
		pagePaginate.setPageNumber(pageNo);
		pagePaginate.setTotalRow(totallyRow);
		pagePaginate.setList(windowTaskItemsVOs);

		return pagePaginate;
	}

}