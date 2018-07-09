package com.jimi.uw_server.controller;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
//import com.jimi.uw_server.annotation.Access;
import com.jimi.uw_server.service.RobotService;
//import com.jimi.uw_server.service.base.SelectService;
import com.jimi.uw_server.util.ResultUtil;

/**
 * 叉车控制层
 * @author HardyYao
 * @createTime 2018年6月8日
 */
public class RobotController extends Controller {

//	private static SelectService daoService = Enhancer.enhance(SelectService.class);
	
	private static RobotService robotService = Enhancer.enhance(RobotService.class);
	
	public void select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		renderJson(ResultUtil.succeed(robotService.select(pageNo, pageSize, ascBy, descBy, filter)));
	}
	
//	@Access({"SuperAdmin"})
	@ActionKey("/manage/robot/switch")
	public void robotSwitch(Integer id, Integer enabled) {
		if(robotService.robotSwitch(id, enabled)) {
			renderJson(ResultUtil.succeed());
		} else {
			renderJson(ResultUtil.failed());
		}
	}
	
}
