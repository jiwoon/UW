package com.jimi.agv_mock.disturber;

import java.util.Random;

import com.jimi.agv_mock.constant.Constant;
import com.jimi.agv_mock.dao.WindowDAO;
import com.jimi.agv_mock.handle.ExceptionHandler;
import com.jimi.agv_mock.socket.MockMainSocket;
import com.jimi.agv_mock.thread.TaskExcutor;

/**
 * 叉车任务干扰器
 * <br>
 * <b>2018年7月20日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class TaskDisturber {

	private TaskExcutor excutor;
	
	
	public TaskDisturber(TaskExcutor excutor) {
		this.excutor = excutor;
	}
	
	
	public void disturbStart() throws InterruptedException {
		long delay = (long) (Constant.START_CMD_DELAY * (1 + ((new Random().nextInt() % (Constant.TASK_FLOATING_PERCENTAGE + 1)) / 100.0)));
		delay(delay);
	}


	public void disturbFirstAction() throws InterruptedException {
		long delay = (long) (Constant.FIRST_ACTION_DELAY * (1 + ((new Random().nextInt() % (Constant.TASK_FLOATING_PERCENTAGE + 1)) / 100.0)));
		delay(delay);
		//一定概率出负载异常
		randomLoadException();
	}
	
	
	public void disturbSecondAction() throws InterruptedException {
		long delay = (long) (Constant.SECOND_ACTION_DELAY * (1 + ((new Random().nextInt() % (Constant.TASK_FLOATING_PERCENTAGE + 1)) / 100.0)));
		delay(delay);
		
		//获取站点坐标
		String missionGroupId = excutor.getMoveCmd().getMissiongroups().get(0).getMissiongroupid();
		int taskId = Integer.parseInt(missionGroupId.split(":")[1]);
		int windowId = WindowDAO.getWindowId(taskId);
		//判断指令类型
		if(excutor.getMoveCmd().getCmdcode().equals("LS")) {
			//抢占站点
			occupyStation(windowId);
		}else if(excutor.getMoveCmd().getCmdcode().equals("SL")){
			//释放站点
			releaseStation(windowId);
		}
	}


	private void releaseStation(int windowId) {
		synchronized (MockMainSocket.getWindows()) {
			MockMainSocket.getWindows().put(windowId, false);
		}
	}


	private void occupyStation(int windowId) throws InterruptedException {
		while(true) {
			synchronized (MockMainSocket.getWindows()) {
				//获取停泊情况
				Boolean parking = MockMainSocket.getWindows().get(windowId);
				if (parking == null || parking == false) {
					//停泊并更新
					MockMainSocket.getWindows().put(windowId, true);
					break;
				}
			}
			Thread.sleep(1000);
		}
	}
	
	
	private void delay(long delay) throws InterruptedException {
		for (long i = 0; i < delay / 1000; i++) {
			//正常耗时
			Thread.sleep(1000);
			
			//一定概率出普通异常
			randomException();
			
			//暂停判断
			while(excutor.getRobot().getSystem_pause()) {
				Thread.sleep(1000);
			}
		}
		Thread.sleep(delay % 1000);
	}


	private void randomException() {
		if(Constant.DISTURB_SWITCH) {
			int a = Math.abs(new Random().nextInt() % 100);
			if(a < Constant.EXCEPTION_PERCENTAGE) {
				int robotid = excutor.getRobot().getRobotid();
				int errorCode = Math.abs(new Random().nextInt() % 26);
				String missionGroupId = excutor.getMoveCmd().getMissiongroups().get(0).getMissiongroupid();
				ExceptionHandler.sendException(robotid, errorCode, missionGroupId);
			}
		}
	}
	
	
	private void randomLoadException() {
		if(Constant.DISTURB_SWITCH) {
			int a = Math.abs(new Random().nextInt() % 100);
			if(a < Constant.LOAD_EXCEPTION_PERCENTAGE) {
				int robotid = excutor.getRobot().getRobotid();
				String missionGroupId = excutor.getMoveCmd().getMissiongroups().get(0).getMissiongroupid();
				ExceptionHandler.sendLoadException(robotid, missionGroupId);
				//中断该任务
				excutor.interrupt();
			}
		}
	}

}
