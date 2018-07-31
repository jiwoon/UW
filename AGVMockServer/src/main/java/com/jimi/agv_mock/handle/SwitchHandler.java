package com.jimi.agv_mock.handle;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.jimi.agv_mock.entity.bo.AGVRobot;
import com.jimi.agv_mock.entity.cmd.AGVSwitchEnableCmd;
import com.jimi.agv_mock.entity.cmd.base.AGVBaseCmd;
import com.jimi.agv_mock.socket.MockRobotInfoSocket;

/**
 * 启用、禁用、暂停、继续处理器
 * <br>
 * <b>2018年7月13日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class SwitchHandler {

	public static void handleEnableOrDisable(String message) {
		AGVSwitchEnableCmd cmd = JSON.parseObject(message, AGVSwitchEnableCmd.class);
		Map<Integer, AGVRobot> robots = MockRobotInfoSocket.getRobots();
		if(cmd.getCmdcode().equals("disable")) {
			for(Integer id : cmd.getRobotids()) {
				robots.get(id).setEnable(1);
			}
		}else {
			for(Integer id : cmd.getRobotids()) {
				robots.get(id).setEnable(2);
			}
		}
	}
	
	
	public static void handlePasueOrStart(String message) {
		AGVBaseCmd cmd = JSON.parseObject(message, AGVBaseCmd.class);
		if(cmd.getCmdcode().equals("allpause")) {
			pauseOrStartAll(true);
		}else {
			pauseOrStartAll(false);
		}
	}
	
	
	public static void pauseOrStartAll(boolean pause) {
		Map<Integer, AGVRobot> robots = MockRobotInfoSocket.getRobots();
		if(pause) {
			for (AGVRobot robot : robots.values()) {
				robot.setSystem_pause(false);
			}
		}else {
			for (AGVRobot robot : robots.values()) {
				robot.setSystem_pause(true);
				//普通异常码归255
				robot.setErrorcode(255);
			}
		}
	}
}
