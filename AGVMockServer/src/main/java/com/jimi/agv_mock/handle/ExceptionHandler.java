package com.jimi.agv_mock.handle;

import com.alibaba.fastjson.JSON;
import com.jimi.agv_mock.entity.bo.AGVRobot;
import com.jimi.agv_mock.entity.cmd.AGVExceptionCmd;
import com.jimi.agv_mock.entity.cmd.AGVLoadExceptionCmd;
import com.jimi.agv_mock.socket.MockMainSocket;
import com.jimi.agv_mock.socket.MockRobotInfoSocket;

/**
 * 异常处理器
 * <br>
 * <b>2018年7月13日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class ExceptionHandler {
	
	public static void sendException(int robotid, int errorCode, String missionGroupId) {
		//暂停系统
		SwitchHandler.pauseOrStartAll(true);
		
		//发送异常信息
		AGVExceptionCmd cmd = new AGVExceptionCmd();
		cmd.setCmdid(MockMainSocket.getCmdId());
		cmd.setCmdcode("exception");
		cmd.setErrorcode(errorCode);
		cmd.setRobotid(robotid);
		cmd.setMissiongroupid(missionGroupId);
		MockMainSocket.sendMessage(JSON.toJSONString(cmd));
		
		//修改叉车状态
		AGVRobot robot = MockRobotInfoSocket.getRobots().get(robotid);
		robot.setErrorcode(errorCode);
	}
	
	
	public static void sendLoadException(int robotId, String missionGroupId) {
		//暂停系统
		SwitchHandler.pauseOrStartAll(true);
		
		//发送异常信息
		AGVLoadExceptionCmd cmd = new AGVLoadExceptionCmd();
		cmd.setCmdid(MockMainSocket.getCmdId());
		cmd.setCmdcode("loadexception");
		cmd.setRobotid(robotId);
		cmd.setMissiongroupid(missionGroupId);
		MockMainSocket.sendMessage(JSON.toJSONString(cmd));
	}
	
}
