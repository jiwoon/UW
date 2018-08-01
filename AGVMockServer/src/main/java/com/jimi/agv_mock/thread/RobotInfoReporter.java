package com.jimi.agv_mock.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSON;
import com.jimi.agv_mock.constant.Constant;
import com.jimi.agv_mock.entity.bo.AGVRobot;
import com.jimi.agv_mock.entity.cmd.AGVRobotInfoCmd;
import com.jimi.agv_mock.socket.MockRobotInfoSocket;

/**
 * 叉车信息实时发送线程
 * <br>
 * <b>2018年7月4日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class RobotInfoReporter extends Thread{

	/**
	 * 机器实体集合
	 */
	private Map<Integer, AGVRobot> robots;
	
	
	public RobotInfoReporter(Map<Integer, AGVRobot> robots) {
		this.robots = robots;
	}

	
	@Override
	public void run() {
		try {
			while(true) {
				sleep(Constant.ROBOT_INFO_CYCLE);
				updateXYPowerInfo();
				sendRobotsInfo();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void sendRobotsInfo() {
		AGVRobotInfoCmd cmd = new AGVRobotInfoCmd();
		cmd.setCmdcode("robotinfo");
		cmd.setCmdid(0);
		List<AGVRobot> agvRobots = new ArrayList<>();
		for (AGVRobot robot : robots.values()) {
			agvRobots.add(robot);
		}
		cmd.setRobotarray(agvRobots);
		MockRobotInfoSocket.sendMessage(JSON.toJSONString(cmd));
	}
	

	private void updateXYPowerInfo() {
		for (AGVRobot robot : robots.values()) {
			if(robot.getSystem_pause() && robot.getEnable() == 2) {
				robot.setBatteryPower(Math.abs(new Random().nextInt() % 100));
				robot.setPosX(Math.abs(new Random().nextInt() % 40));
				robot.setPosY(Math.abs(new Random().nextInt() % 30));
			}
		}
	}


}
