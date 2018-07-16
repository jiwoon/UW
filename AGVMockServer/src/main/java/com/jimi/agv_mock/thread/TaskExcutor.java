package com.jimi.agv_mock.thread;

import java.util.Random;

import com.alibaba.fastjson.JSON;
import com.jimi.agv_mock.constant.Constant;
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
			AGVStatusCmd statusCmd = new AGVStatusCmd();
			
			//判断SL or LS
			if(moveCmd.getCmdcode().equals("SL")){
				statusCmd.setRobotid(moveCmd.getMissiongroups().get(0).getRobotid());
			}else {
				statusCmd.setRobotid(robot.getRobotid());
			}
			
			//设置参数
			statusCmd.setCmdcode("status");
			statusCmd.setMissiongroupid(moveCmd.getMissiongroups().get(0).getMissiongroupid());
			
			//延迟并发送已开始任务状态指令
			Thread.sleep((long) (Constant.START_CMD_DELAY * (1 + ((new Random().nextInt() 
					% Constant.FLOATING_PERCENTAGE) / 100.0))));
			statusCmd.setCmdid(MockMainSocket.getCmdId());
			statusCmd.setStatus(0);
			MockMainSocket.sendMessage((JSON.toJSONString(statusCmd)));
			
			//判断系统是否被暂停
			while(robot.getSystem_pause()) {
				Thread.sleep(1000);
			}
			
			//延迟并发送已完成第一动作状态指令
			Thread.sleep((long) (Constant.FIRST_ACTION_DELAY * (1 + ((new Random().nextInt() 
					% Constant.FLOATING_PERCENTAGE) / 100.0))));
			statusCmd.setCmdid(MockMainSocket.getCmdId());
			statusCmd.setStatus(1);
			MockMainSocket.sendMessage(JSON.toJSONString(statusCmd));
			
			//判断系统是否被暂停
			while(robot.getSystem_pause()) {
				Thread.sleep(1000);
			}
			
			//延迟并发送已完成第二动作状态指令
			Thread.sleep((long) (Constant.SECOND_ACTION_DELAY * (1 + ((new Random().nextInt() 
					% Constant.FLOATING_PERCENTAGE) / 100.0))));
			statusCmd.setCmdid(MockMainSocket.getCmdId());
			statusCmd.setStatus(2);
			MockMainSocket.sendMessage(JSON.toJSONString(statusCmd));
			
			//判断系统是否被暂停
			while(robot.getSystem_pause()) {
				Thread.sleep(1000);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
