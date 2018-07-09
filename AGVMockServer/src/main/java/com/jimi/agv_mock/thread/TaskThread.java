package com.jimi.agv_mock.thread;

import java.util.Random;

import com.alibaba.fastjson.JSON;
import com.jimi.agv_mock.constant.Constant;
import com.jimi.agv_mock.entity.AGVMoveCmd;
import com.jimi.agv_mock.entity.AGVStatusCmd;
import com.jimi.agv_mock.socket.MockWebSocket;

/**
 * 模拟叉车执行任务的线程
 * <br>
 * <b>2018年7月4日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class TaskThread extends Thread{

	private AGVMoveCmd moveCmd;
	
	private MockWebSocket socket;

	public TaskThread(MockWebSocket socket, AGVMoveCmd moveCmd) {
		this.socket = socket;
		this.moveCmd = moveCmd;
	}
	
	
	@Override
	public void run() {
		int robotid = Constant.ROBOT_IDS[Math.abs(new Random().nextInt()) % 2];
		AGVStatusCmd statusCmd = new AGVStatusCmd();
		if(moveCmd.getCmdcode().equals("SL")){
			statusCmd.setRobotid(moveCmd.getMissiongroups().get(0).getRobotid());
		}else {
			statusCmd.setRobotid(robotid);
		}
		statusCmd.setCmdcode("status");
		statusCmd.setMissiongroupid(moveCmd.getMissiongroups().get(0).getMissiongroupid());
		
		
		try {
			sleep((long) (Constant.GET_CMD_DELAY * (1 + ((new Random().nextInt() 
					% Constant.FLOATING_PERCENTAGE) / 100.0))));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		statusCmd.setCmdid(socket.getCmdId());
		statusCmd.setStatus(0);
		socket.sendMessage(JSON.toJSONString(statusCmd));
		
		try {
			sleep((long) (Constant.FIRST_ACTION_DELAY * (1 + ((new Random().nextInt() 
					% Constant.FLOATING_PERCENTAGE) / 100.0))));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		statusCmd.setCmdid(socket.getCmdId());
		statusCmd.setStatus(1);
		socket.sendMessage(JSON.toJSONString(statusCmd));
		
		try {
			sleep((long) (Constant.SECOND_ACTION_DELAY * (1 + ((new Random().nextInt() 
					% Constant.FLOATING_PERCENTAGE) / 100.0))));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		statusCmd.setCmdid(socket.getCmdId());
		statusCmd.setStatus(2);
		socket.sendMessage(JSON.toJSONString(statusCmd));
	}
	
}
