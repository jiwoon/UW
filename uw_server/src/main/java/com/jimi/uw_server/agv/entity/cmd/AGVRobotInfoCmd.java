package com.jimi.uw_server.agv.entity.cmd;

import java.util.List;

import com.jimi.uw_server.agv.entity.bo.AGVRobot;
import com.jimi.uw_server.agv.entity.cmd.base.AGVBaseCmd;

/**
 * AGV机器实时信息类
 * <br>
 * <b>2018年7月13日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AGVRobotInfoCmd extends AGVBaseCmd{
	
	private List<AGVRobot> robotarry;

	public List<AGVRobot> getRobotarry() {
		return robotarry;
	}

	public void setRobotarry(List<AGVRobot> robotarry) {
		this.robotarry = robotarry;
	}
	
}
