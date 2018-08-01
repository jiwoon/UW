package com.jimi.agv_mock.thread;

import com.alibaba.fastjson.JSON;
import com.jimi.agv_mock.disturber.TaskDisturber;
import com.jimi.agv_mock.entity.bo.AGVRobot;
import com.jimi.agv_mock.entity.cmd.AGVMoveCmd;
import com.jimi.agv_mock.entity.cmd.AGVStatusCmd;
import com.jimi.agv_mock.socket.MockMainSocket;

/**
 * 模拟叉车执行LS，SL任务的线程
 * <br>
 * <b>2018年7月4日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class TaskExcutor extends Thread{

	/**
	 * 任务指令
	 */
	private AGVMoveCmd moveCmd;
	
	/**
	 * 执行任务的机器
	 */
	private AGVRobot robot;

	
	public TaskExcutor(AGVRobot robot, AGVMoveCmd moveCmd) {
		this.robot = robot;
		this.moveCmd = moveCmd;
	}
	
	
	@Override
	public void run() {
		try {
			//设置叉车忙碌
			robot.setStatus(1);
			
			AGVStatusCmd statusCmd = new AGVStatusCmd();
			
			//设置robotid
			statusCmd.setRobotid(robot.getRobotid());
			
			//设置参数
			statusCmd.setCmdcode("status");
			statusCmd.setMissiongroupid(moveCmd.getMissiongroups().get(0).getMissiongroupid());
			
			//创建干扰者
			TaskDisturber disturber = new TaskDisturber(this);
			
			//LS
			exe(statusCmd, disturber);
			
			//请求SL命令，等待命令到达
			while(!MockMainSocket.getTaskPool().requestSLTask(this)) {
				sleep(1000);
			}
			
			//转换命令类型
			moveCmd.setCmdcode("SL");
			
			//SL
			exe(statusCmd, disturber);
			
			//设置叉车空闲
			robot.setStatus(0);
			
		} catch (InterruptedException e) {
			System.out.println("A task has been interrupted");
		}
	}


	private void exe(AGVStatusCmd statusCmd, TaskDisturber disturber) throws InterruptedException {
		//延迟并发送已开始任务状态指令
		disturber.disturbStart();
		statusCmd.setCmdid(MockMainSocket.getCmdId());
		statusCmd.setStatus(0);
		MockMainSocket.sendMessage((JSON.toJSONString(statusCmd)));
		
		//延迟并发送已完成第一动作状态指令
		disturber.disturbFirstAction();
		statusCmd.setCmdid(MockMainSocket.getCmdId());
		statusCmd.setStatus(1);
		MockMainSocket.sendMessage(JSON.toJSONString(statusCmd));
		
		//延迟并发送已完成第二动作状态指令
		disturber.disturbSecondAction();
		statusCmd.setCmdid(MockMainSocket.getCmdId());
		statusCmd.setStatus(2);
		MockMainSocket.sendMessage(JSON.toJSONString(statusCmd));
	}


	public AGVMoveCmd getMoveCmd() {
		return moveCmd;
	}


	public AGVRobot getRobot() {
		return robot;
	}
	
}
