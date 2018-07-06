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

public class RobotService extends SelectService {
	
	private static SelectService selectService = Enhancer.enhance(SelectService.class);
	
	public Object select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		List<RobotVO> robotVO = new ArrayList<RobotVO>();
		
		Page<Record> result = selectService.select("robot", pageNo, pageSize, ascBy, descBy, filter);
		
		int totallyRow =  0;
		for (Record res : result.getList()) {
			RobotVO r = new RobotVO(res.get("id"), res.get("status"), res.get("battery"),
					res.get("x"), res.get("y"), res.get("enabled"));
			robotVO.add(r);
			totallyRow++;
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
		robot.keep("id", "status", "battery", "x", "y", "enabled");
		return robot.update();
	}

}
