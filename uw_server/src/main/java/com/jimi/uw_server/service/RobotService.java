package com.jimi.uw_server.service;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jimi.uw_server.agv.dao.RobotInfoRedisDAO;
import com.jimi.uw_server.agv.dao.TaskItemRedisDAO;
import com.jimi.uw_server.agv.entity.bo.AGVIOTaskItem;
import com.jimi.uw_server.agv.handle.LSSLHandler;
import com.jimi.uw_server.agv.handle.SwitchHandler;
import com.jimi.uw_server.model.bo.RobotBO;
import com.jimi.uw_server.model.vo.RobotVO;
import com.jimi.uw_server.service.base.SelectService;

/**
 * 叉车业务层
 * 
 * @author HardyYao
 * @createTime 2018年6月8日
 */
public class RobotService extends SelectService {

	private static TaskService taskService = Enhancer.enhance(TaskService.class);

	public static final String GET_ALL_ROBOT_IDS_SQL = "SELECT id FROM robot";

	
	public List<RobotVO> select() {
		List<RobotBO> robotBOs = RobotInfoRedisDAO.check();
		List<RobotVO> robotVOs = new ArrayList<>();
		for (RobotBO robotBO : robotBOs) {
			RobotVO robotVO = new RobotVO(robotBO);
			robotVOs.add(robotVO);
		}
		return robotVOs;
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
            RobotInfoRedisDAO.clearLoadException();
        } else {
            SwitchHandler.sendAllPause();
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
