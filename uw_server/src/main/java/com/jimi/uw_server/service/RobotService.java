package com.jimi.uw_server.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jimi.uw_server.agv.entity.bo.AGVRobot;
import com.jimi.uw_server.model.Robot;
import com.jimi.uw_server.model.vo.RobotVO;
import com.jimi.uw_server.service.base.SelectService;
import com.jimi.uw_server.service.entity.PagePaginate;

/**
 * 叉车业务层
 * @author HardyYao
 * @createTime 2018年6月8日
 */
public class RobotService extends SelectService {

	private static SelectService selectService = Enhancer.enhance(SelectService.class);
	
	public static final String getRobotAllId = "SELECT id FROM robot";

	public Object select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		List<RobotVO> robotVO = new ArrayList<RobotVO>();

		Page<Record> result = selectService.select("robot", pageNo, pageSize, ascBy, descBy, filter);

		int totallyRow =  result.getTotalRow();
		for (Record res : result.getList()) {
			RobotVO r = new RobotVO(res.get("id"), res.get("status"), res.get("battery"),
					res.get("x"), res.get("y"), res.get("enabled"), res.get("error"), res.get("warn"), res.get("pause"));
			robotVO.add(r);
		}

		PagePaginate pagePaginate = new PagePaginate();
		pagePaginate.setPageNumber(pageNo);
		pagePaginate.setPageSize(pageSize);
		pagePaginate.setTotalRow(totallyRow);
		
		pagePaginate.setList(robotVO);
		
		return pagePaginate;
	}

	public boolean robotSwitch(Integer id, Integer enabled) {
		Robot robot = new Robot();
		robot.setId(id);
		robot.setEnabled(enabled);
		robot.keep("id", "status", "battery", "x", "y", "enabled", "error", "warn", "pause");
		return robot.update();
	}
	
	public boolean pause(boolean pause) {
		Robot robot = new Robot();
		List<Robot> robotId = Robot.dao.find(getRobotAllId);
		Integer id;
		boolean res = true;
		for (Robot rId : robotId) {
			id = rId.getId();
			robot.setId(id);
			robot.setPause(pause);
			robot.keep("id", "status", "battery", "x", "y", "enabled", "error", "warn", "pause");
			res = robot.update();
		}
		return res;
	}

	
	public void updateRobotInfo(Map<Integer, AGVRobot> newRobots, Map<Integer, AGVRobot> robots) {
		//获取新增项
		Set<Integer> addRobotsIds = new HashSet<>(newRobots.keySet());
		addRobotsIds.removeAll(robots.keySet());
		//新增机器记录
		for (Integer id : addRobotsIds) {
			Robot robot = AGVRobot.toModel(newRobots.get(id));
			robot.save();
		}
		
		//获取减少项
		Set<Integer> removeRobotsIds = new HashSet<>(robots.keySet());
		removeRobotsIds.removeAll(newRobots.keySet());
		//删除机器记录
		for (Integer id : removeRobotsIds) {
			Robot.dao.deleteById(id);
		}
		
		//获取修改项
		Set<Integer> modifyRobotsIds = new HashSet<>(newRobots.keySet());
		modifyRobotsIds.retainAll(robots.keySet());
		//修改机器记录
		for (Integer id : modifyRobotsIds) {
			Robot robot = AGVRobot.toModel(newRobots.get(id));
			robot.update();
		}
	}
	
}
