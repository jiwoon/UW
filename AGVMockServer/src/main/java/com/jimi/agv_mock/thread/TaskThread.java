package com.jimi.agv_mock.thread;

import java.util.Random;

import com.alibaba.fastjson.JSON;
import com.jimi.agv_mock.constant.Constant;
import com.jimi.agv_mock.entity.AGVBaseCmd;
import com.jimi.agv_mock.entity.AGVMoveCmd;
import com.jimi.agv_mock.entity.AGVStatusCmd;
import com.jimi.agv_mock.socket.MockWebSocket;

/**
 * 模拟叉车执行LS，SL任务的线程
 * <br>
 * <b>2018年7月4日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class TaskThread extends Thread{

	private AGVMoveCmd moveCmd;
	
	private MockWebSocket socket;
	
	/**
	 * 正在等待ACK指令的ID
	 */
	private int waitingAckCmdId;

	public TaskThread(MockWebSocket socket, AGVMoveCmd moveCmd) {
		this.socket = socket;
		this.moveCmd = moveCmd;
	}
	
	
	@Override
	public void run() {
		try {
			int robotid = Constant.ROBOT_IDS[Math.abs(new Random().nextInt()) % 2];
			AGVStatusCmd statusCmd = new AGVStatusCmd();
			if(moveCmd.getCmdcode().equals("SL")){
				statusCmd.setRobotid(moveCmd.getMissiongroups().get(0).getRobotid());
			}else {
				statusCmd.setRobotid(robotid);
			}
			statusCmd.setCmdcode("status");
			statusCmd.setMissiongroupid(moveCmd.getMissiongroups().get(0).getMissiongroupid());
			
			sleep((long) (Constant.GET_CMD_DELAY * (1 + ((new Random().nextInt() 
					% Constant.FLOATING_PERCENTAGE) / 100.0))));
			statusCmd.setCmdid(socket.getCmdId());
			statusCmd.setStatus(0);
			sendAndWaitingAck(JSON.toJSONString(statusCmd));
			
			sleep((long) (Constant.FIRST_ACTION_DELAY * (1 + ((new Random().nextInt() 
					% Constant.FLOATING_PERCENTAGE) / 100.0))));
			statusCmd.setCmdid(socket.getCmdId());
			statusCmd.setStatus(1);
			sendAndWaitingAck(JSON.toJSONString(statusCmd));
			
			sleep((long) (Constant.SECOND_ACTION_DELAY * (1 + ((new Random().nextInt() 
					% Constant.FLOATING_PERCENTAGE) / 100.0))));
			statusCmd.setCmdid(socket.getCmdId());
			statusCmd.setStatus(2);
			sendAndWaitingAck(JSON.toJSONString(statusCmd));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	private void sendAndWaitingAck(String message) throws InterruptedException {
		AGVBaseCmd baseCmd = JSON.parseObject(message, AGVBaseCmd.class);
		waitingAckCmdId = baseCmd.getCmdid();
		socket.getSendCmdidAckMap().put(waitingAckCmdId, false);
		socket.sendMessage(message);
		
		sleep(Constant.WAIT_ACK_TIMEOUT);
		while(!socket.getSendCmdidAckMap().get(waitingAckCmdId)) {
			socket.sendMessage(message);
			sleep(Constant.WAIT_ACK_TIMEOUT);
		}
	}
	
}
