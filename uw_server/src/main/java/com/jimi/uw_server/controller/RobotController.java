package com.jimi.uw_server.controller;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jimi.uw_server.service.base.SelectService;
import com.jimi.uw_server.util.ResultUtil;

public class RobotController extends Controller {

	private static SelectService daoService = Enhancer.enhance(SelectService.class);
	
	public void select(String table, Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		renderJson(ResultUtil.succeed(daoService.select(table, pageNo, pageSize, ascBy, descBy, filter)));
	}
	
	//@Access({"SuperAdmin"})
	public void robotSwitch(Integer id, boolean enabled) {
		
	}
	
}
