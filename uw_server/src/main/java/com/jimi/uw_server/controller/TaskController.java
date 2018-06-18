package com.jimi.uw_server.controller;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import com.jimi.uw_server.model.Task;
import com.jimi.uw_server.service.TaskService;
import com.jimi.uw_server.service.base.SelectService;
import com.jimi.uw_server.util.ResultUtil;

public class TaskController extends Controller {

	private static SelectService daoService = Enhancer.enhance(SelectService.class);
	
	private static TaskService taskService = Enhancer.enhance(TaskService.class);
	
//	private static String checkTaskSql = "SELECT material_type.no as materialNo, packing_list_item.quantity as requestQuantity,"
//			+ "task_log.quantity as actualQuantity, task_log.time as finishTime FROM material_type,packing_list_item,task_log "
//			+ "where task_log.id = ? material_type.id = packing_list_item.material_type_id"
//			+ "and packing_list_item.task_id = task_log.task_id";
	
	//@Access({"SuperAdmin"})
	public void create(@Para("") Task task, Integer type, String fileName) {
		if(taskService.create(task, type, fileName)) {
			renderJson(ResultUtil.succeed());
		} else {
			renderJson(ResultUtil.failed());
		}
	}
	
	//@Access({"SuperAdmin"})
	public void pass(@Para("") Task task, Integer id) {
		if(taskService.pass(task, id)) {
			renderJson(ResultUtil.succeed());
		} else {
			renderJson(ResultUtil.failed());
		}
	}
	
	//@Access({"SuperAdmin"})
	public void start(@Para("") Task task, Integer id, Integer window) {
		if(taskService.start(task, id, window)) {
			renderJson(ResultUtil.succeed());
		} else {
			renderJson(ResultUtil.failed());
		}
	}
	
	//@Access({"SuperAdmin"})
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
		String table = "task";
		renderJson(ResultUtil.succeed(daoService.select(table, pageNo, pageSize, ascBy, descBy, filter)));
	}
	
}
