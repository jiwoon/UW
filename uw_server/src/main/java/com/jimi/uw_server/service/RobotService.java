package com.jimi.uw_server.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jimi.uw_server.agv.dao.TaskItemRedisDAO;
import com.jimi.uw_server.agv.entity.bo.AGVIOTaskItem;
import com.jimi.uw_server.agv.entity.bo.AGVRobot;
import com.jimi.uw_server.agv.handle.LSSLHandler;
import com.jimi.uw_server.agv.handle.SwitchHandler;
import com.jimi.uw_server.model.Robot;
import com.jimi.uw_server.model.vo.RobotVO;
import com.jimi.uw_server.service.base.SelectService;
import com.jimi.uw_server.service.entity.PagePaginate;

/**
 * 叉车业务层
 * 
 * @author HardyYao
 * @createTime 2018年6月8日
 */
public class RobotService extends SelectService {

	private static SelectService selectService = Enhancer.enhance(SelectService.class);
	
	private static TaskService taskService = Enhancer.enhance(TaskService.class);

	public static final String GET_ALL_ROBOT_IDS_SQL = "SELECT id FROM robot";

	
	public Object select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter) {
		List<RobotVO> robotVOs = new ArrayList<RobotVO>();

		Page<Record> result = selectService.select("robot", pageNo, pageSize, ascBy, descBy, filter);

		int totallyRow = result.getTotalRow();
		for (Record res : result.getList()) {
			RobotVO r = new RobotVO(res.get("id"), res.get("status"), res.get("battery"), res.get("x"), res.get("y"), 
					res.get("enabled"), res.get("error"), res.get("warn"), res.get("pause"), res.get("load_exception"));
			robotVOs.add(r);
		}

		PagePaginate pagePaginate = new PagePaginate();
		pagePaginate.setPageNumber(pageNo);
		pagePaginate.setPageSize(pageSize);
		pagePaginate.setTotalRow(totallyRow);

		pagePaginate.setList(robotVOs);

		return pagePaginate;
	}

	
	public void robotSwitch(String id, Integer enabled) throws Exception {
		List<Integer> idList = new ArrayList<>();
		String[] ids = id.split(",");
		for (String string : ids) {
			idList.add(Integer.parseInt(string));
		}
		if (enabled == 2) {
			SwitchHandler.sendEnable(idList);
		} else if (enabled == 1) {
			SwitchHandler.sendDisable(idList);
		}
	}

	
	public void pause(Boolean pause) throws Exception {
        if (pause) {
            SwitchHandler.sendAllStart();
            clearLoadException();
        } else {
            SwitchHandler.sendAllPause();
        }
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

	
	/**
	 * 设置某台机器的负载异常为真
	 */
	public void setloadException(int id) {
		Robot robot = new Robot();
		robot.setId(id);
		robot.setLoadException(true);
		robot.update();
	}
	
	
	/**
	 * 清除全部负载异常
	 */
	public void clearLoadException() {
		List<Robot> robots = Robot.dao.find(GET_ALL_ROBOT_IDS_SQL);
		for (Robot robot : robots) {
			robot.setLoadException(false);
			robot.update();
		}
	}


	/**
	 * 叉车回库SL
	 */
	public void back(Integer id) throws Exception {
		for (AGVIOTaskItem item : TaskItemRedisDAO.getTaskItems()) {
			if(id.equals(item.getId())) {
				LSSLHandler.sendSL(item);
				taskService.finishItem(item.getId());
			}
		}
	}

}
