package com.jimi.uw_server.controller;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import com.jfinal.json.Json;
import com.jfinal.upload.UploadFile;
import com.jimi.uw_server.exception.OperationException;
import com.jimi.uw_server.model.MaterialType;
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

	public void create(@Para("") Task task, @Para("") PackingListItem packingListItem, @Para("") MaterialType materialType, UploadFile file, Integer type) throws Exception {
		file = getFile();
		String fileName = file.getFileName();
		String fullFileName = file.getUploadPath() + "\\" + file.getFileName();
		if(taskService.create(task, type, fileName)) {
			if (TaskService.insertPackingList(task, packingListItem, materialType, type, fullFileName)) {
				renderJson(ResultUtil.succeed());
			} else {
				renderJson(ResultUtil.failed(412));
				throw new OperationException("创建任务失败，套料单格式错误或者物料类型表里面不存在套料单中对应的物料类型！");
			}
		}
	}

	public void pass(@Para("") Task task, Integer id) {
		if(taskService.pass(task, id)) {
			renderJson(ResultUtil.succeed());
		} else {
			renderJson(ResultUtil.failed());
		}
	}

	public void start(@Para("") Task task, Integer id, Integer window) {
		if(taskService.start(task, id, window)) {
			renderJson(ResultUtil.succeed());
		} else {
			renderJson(ResultUtil.failed());
		}
	}
	
	public void cancel(@Para("") Task task, Integer id) {
		if(taskService.cancel(task, id)) {
			renderJson(ResultUtil.succeed());
		} else {
			renderJson(ResultUtil.failed());
		}
	}

//	public void check(Integer id) {
//		renderJson(ResultUtil.succeed(taskService.check(id)));
//	}
	
	public void select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		Json.getJson().toJson(ResultUtil.succeed(taskService.select(pageNo, pageSize, ascBy, descBy, filter)));
		renderJson(ResultUtil.succeed(taskService.select(pageNo, pageSize, ascBy, descBy, filter)));
	}
	
	public void getWindows(@Para("") Window window) {
		renderJson(ResultUtil.succeed(taskService.getWindows(window)));
	}
	
}
