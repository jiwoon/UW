package com.jimi.agv_mock.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import com.jimi.agv_mock.constant.Constant;
import com.jimi.agv_mock.entity.bo.AGVRobot;
import com.jimi.agv_mock.entity.cmd.AGVMoveCmd;
import com.jimi.agv_mock.socket.MockRobotInfoSocket;

/**
 * 任务池，负责存储任务，分配任务
 * <br>
 * <b>2018年7月13日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class TaskPool extends Thread{

	private Queue<AGVMoveCmd> tasks;
	
	
	public TaskPool() {
		tasks = new LinkedBlockingQueue<>();
	}
	
	
	public synchronized void addTask(AGVMoveCmd cmd) {
		tasks.offer(cmd);
	}
	
	
	@Override
	public void run() {
		System.out.println("TaskPool is running NOW...");
		while(true) {
			try {
				sleep(Constant.TASK_POOL_CYCLE);
				assignOneTask();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private synchronized void assignOneTask() {
		AGVMoveCmd task = tasks.peek();
		if(task == null) {
			return; 
		}
		AGVRobot robot = null;
		//判断被绑定的robotid
		int robotid = task.getMissiongroups().get(0).getRobotid();
		if(robotid == 0) {
			robot = getAFreeRobot();
		}else {
			robot = getRobotById(robotid);
		}
		//如果取得Robot则出列
		if(robot != null) {
			tasks.poll();
			TaskExcutor excutor = new TaskExcutor(getAFreeRobot(), task);
			excutor.start();
		}
	}
	
	
	/**
	 * 随机获取一个空闲的机器
	 */
	private AGVRobot getAFreeRobot() {
		List<AGVRobot> freeRobotIds = new ArrayList<>();
		for (AGVRobot robot : MockRobotInfoSocket.getRobots().values()) {
			//筛选空闲或充电状态的处于启用中的叉车
			if((robot.getStatus() == 0 || robot.getStatus() == 4) && robot.getEnable() == 2) {
				freeRobotIds.add(robot);
			}
		}
		//随机选择一个
		if(freeRobotIds.size() == 0) {
			return null;
		}else {
			AGVRobot robot = freeRobotIds.get(Math.abs(new Random().nextInt()) % freeRobotIds.size());
			return robot;
		}
	}
	
	
	/**
	 * 获取指定机器，如果非空闲则返回null 
	 */
	private AGVRobot getRobotById(int robotId) {
		AGVRobot robot = MockRobotInfoSocket.getRobots().get(robotId);
		if((robot.getStatus() == 0 || robot.getStatus() == 4) && robot.getEnable() == 2) {
			return robot;
		}else {
			return null;
		}
	}
	
}
