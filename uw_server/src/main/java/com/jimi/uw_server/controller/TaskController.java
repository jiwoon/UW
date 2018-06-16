package com.jimi.uw_server.controller;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jimi.uw_server.service.base.SelectService;
import com.jimi.uw_server.util.ResultUtil;

public class TaskController extends Controller {

	private static SelectService daoService = Enhancer.enhance(SelectService.class);
	
	//@Access({"SuperAdmin"})
	public void create(Integer type, String fileName) {
		
	}
	
	//@Access({"SuperAdmin"})
	public void pass(Integer id) {
		
	}
	
	//@Access({"SuperAdmin"})
	public void start(Integer id, Integer window) {
		
	}
	
	//@Access({"SuperAdmin"})
	public void cancel(Integer id) {
		
	}

	public void check(Integer id) {
		
	}
	
	public void select(String table, Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		renderJson(ResultUtil.succeed(daoService.select(table, pageNo, pageSize, ascBy, descBy, filter)));
	}
	
}
