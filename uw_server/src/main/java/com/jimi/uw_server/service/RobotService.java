package com.jimi.uw_server.service;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
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
					res.get("x"), res.get("y"), res.get("enabled"), res.get("errorcode"), res.get("warncode"), res.get("pause"));
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
		robot.keep("id", "status", "battery", "x", "y", "enabled", "errorcode", "warncode", "pause");
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
			robot.keep("id", "status", "battery", "x", "y", "enabled", "errorcode", "warncode", "pause");
			res = robot.update();
		}
		return res;
	}

}
