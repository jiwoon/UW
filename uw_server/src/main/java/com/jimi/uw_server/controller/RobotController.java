package com.jimi.uw_server.controller;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jimi.uw_server.service.RobotService;
import com.jimi.uw_server.util.ResultUtil;

/**
 * 叉车控制层
 * @author HardyYao
 * @createTime 2018年6月8日
 */
public class RobotController extends Controller {

	private static RobotService robotService = Enhancer.enhance(RobotService.class);

	public void select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		renderJson(ResultUtil.succeed(robotService.select(pageNo, pageSize, ascBy, descBy, filter)));
	}


	@ActionKey("/manage/robot/switch")
	public void robotSwitch(String id, Integer enabled) {
		robotService.robotSwitch(id, enabled);
		renderJson(ResultUtil.succeed());
	}
	

	public void pause(Boolean pause) {
		robotService.pause(pause);
		renderJson(ResultUtil.succeed());
	}
	
	
	public void back(Integer id) {
		robotService.back(id);
		renderJson(ResultUtil.succeed());
	}
}
