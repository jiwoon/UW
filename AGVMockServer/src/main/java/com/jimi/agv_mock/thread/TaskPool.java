package com.jimi.agv_mock.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jimi.agv_mock.entity.bo.AGVRobot;
import com.jimi.agv_mock.socket.MockRobotInfoSocket;

/**
 * 任务池，负责存储任务，分配任务
 * <br>
 * <b>2018年7月13日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class TaskPool extends Thread{

	private static AGVRobot getAFreeRobot() {
		List<AGVRobot> freeRobotIds = new ArrayList<>();
		for (AGVRobot robot : MockRobotInfoSocket.getRobots().values()) {
			if((robot.getStatus() == 0 || robot.getStatus() == 4) && //筛选空闲或充电状态的叉车
					!robot.getSystem_pause() && //判断
					robot.getEnable() == 2) {
				freeRobotIds.add(robot);
			}
		}
		AGVRobot robot = freeRobotIds.get(Math.abs(new Random().nextInt()) % freeRobotIds.size());
		return robot;
	}
	
}
