package com.jimi.uw_server.controller;

import java.io.File;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import com.jfinal.upload.UploadFile;
import com.jimi.uw_server.agv.dao.TaskItemRedisDAO;
import com.jimi.uw_server.agv.entity.bo.AGVIOTaskItem;
import com.jimi.uw_server.model.PackingListItem;
import com.jimi.uw_server.model.Task;
import com.jimi.uw_server.model.Window;
import com.jimi.uw_server.service.TaskService;
import com.jimi.uw_server.util.ResultUtil;

/**
 * 任务控制层
 * @author HardyYao
 * @createTime 2018年6月8日
 */
public class TaskController extends Controller {

	private static TaskService taskService = Enhancer.enhance(TaskService.class);

	public void create(@Para("") Task task, @Para("") PackingListItem packingListItem, UploadFile file, Integer type) throws Exception {
		// 如果是创建「出入库任务」，入库type为0，出库type为1
		if (type.equals(0) || type.equals(1)) {
			file = getFile();
			String fileName = file.getFileName();
			String fullFileName = file.getUploadPath() + File.separator + file.getFileName();
			if(taskService.createIOTask(task, type, fileName, fullFileName)) {
				if (TaskService.insertPackingList(packingListItem, type, fullFileName)) {
					renderJson(ResultUtil.succeed());
				} else {
					renderJson(ResultUtil.failed(412));
				}
			} else {
				renderJson(ResultUtil.failed("创建任务失败，请检查套料单的文件格式及内容格式！"));
			}
		} else if (type.equals(2) ) {	//如果是创建「盘点任务」
			renderJson(ResultUtil.failed("该功能尚在开发中！"));
		} else if (type.equals(3)) {	//如果是创建「位置优化任务」
			renderJson(ResultUtil.failed("该功能尚在开发中！"));
		}
		
	}

	public void pass(@Para("") Task task, Integer id) {
		if(taskService.pass(task, id)) {
			renderJson(ResultUtil.succeed());
		} else {
			renderJson(ResultUtil.failed("审核失败！"));
		}
	}

	public void start(@Para("") Task task, Integer id, Integer window) {
		List<AGVIOTaskItem> items = taskService.start(task, id, window);
		if(!items.isEmpty()) {
			renderJson(ResultUtil.succeed());
			// 把任务条目均匀插入到队列til中
			TaskItemRedisDAO.addTaskItem(items);
		} else {
			renderJson(ResultUtil.failed());
		}
	}
	
	public void cancel(@Para("") Task task, Integer id) {
		if(taskService.cancel(task, id)) {
			renderJson(ResultUtil.succeed());
		} else {
			renderJson(ResultUtil.failed("作废失败！"));
		}
	}

	public void check(Integer id) {
		renderJson(ResultUtil.failed("该功能尚在开发中！"));
	}
	
	public void select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		renderJson(ResultUtil.succeed(taskService.select(pageNo, pageSize, ascBy, descBy, filter)));
	}
	
	public void getWindows(@Para("") Window window) {
		renderJson(ResultUtil.succeed(taskService.getWindows(window)));
	}
	
}
