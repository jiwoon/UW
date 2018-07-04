package com.jimi.uw_server.service;

import java.util.ArrayList;
import java.util.List;

import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.model.Robot;
import com.jimi.uw_server.model.vo.RobotVO;
import com.jimi.uw_server.service.base.SelectService;
import com.jimi.uw_server.service.entity.Page;

public class RobotService extends SelectService {

	private static final String doPaginateSql = "SELECT COUNT(*) as total FROM robot";
	
	private static final String selectRobotSql = "SELECT * FROM robot limit ?,?";
	
	public Object select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		List<Robot> robot;
		List<RobotVO> robotVO = new ArrayList<RobotVO>();
		
		Page page = new Page();
		page.setPageNumber(pageNo);
		page.setPageSize(pageSize);
		Integer totallyRow = Integer.parseInt(MaterialType.dao.findFirst(doPaginateSql).get("total").toString());
		page.setTotalRow(totallyRow);
		Integer firstIndex = (page.getPageNumber()-1)*page.getPageSize();
		robot= Robot.dao.find(selectRobotSql, firstIndex, page.getPageSize());
		
		for (Robot item : robot) {
			RobotVO r = new RobotVO(item.getId(), item.getStatus(), item.getBattery(), item.getX(), item.getY(), item.getEnabled());
			robotVO.add(r);
		}
		
		page.setList(robotVO);
		
		return page;
	}
	
	public boolean robotSwitch(Integer id, Integer enabled) {
		Robot robot = new Robot();
		robot.setId(id);
		robot.setEnabled(enabled);
		robot.keep("id", "status", "battery", "x", "y", "enabled");
		return robot.update();
	}

}
