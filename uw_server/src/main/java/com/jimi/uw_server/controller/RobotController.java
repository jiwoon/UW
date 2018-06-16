package com.jimi.uw_server.controller;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jimi.uw_server.annotation.Access;
import com.jimi.uw_server.service.RobotService;
import com.jimi.uw_server.service.base.SelectService;
import com.jimi.uw_server.util.ResultUtil;

public class RobotController extends Controller {

	private static SelectService daoService = Enhancer.enhance(SelectService.class);
	
	private static RobotService robotService = Enhancer.enhance(RobotService.class);
	
	public void select(String table, Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		table = "robot";
		renderJson(ResultUtil.succeed(daoService.select(table, pageNo, pageSize, ascBy, descBy, filter)));
	}
	
	@Access({"SuperAdmin"})
	public void robotSwitch(Integer id, Integer enabled) {
		if(robotService.robotSwitch(id, enabled)) {
			renderJson(ResultUtil.succeed());
		} else {
			renderJson(ResultUtil.failed());
		}
	}
	
}
