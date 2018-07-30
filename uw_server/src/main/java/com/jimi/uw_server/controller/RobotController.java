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


	// 查询叉车
	public void select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		renderJson(ResultUtil.succeed(robotService.select(pageNo, pageSize, ascBy, descBy, filter)));
	}


	// 启用/禁用叉车
	@ActionKey("/manage/robot/switch")
	public void robotSwitch(String id, Integer enabled) throws Exception {
		robotService.robotSwitch(id, enabled);
		renderJson(ResultUtil.succeed());
	}
	

	// 启动/暂停叉车
	public void pause(Boolean pause) throws Exception {
		robotService.pause(pause);
		renderJson(ResultUtil.succeed());
	}


	// 令叉车回库
//	@Log("将编号为{id}的叉车回库")
	public void back(Integer id) throws Exception {
		robotService.back(id);
		renderJson(ResultUtil.succeed());
	}
}
