package com.jimi.agv_mock.socket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.jimi.agv_mock.constant.Constant;
import com.jimi.agv_mock.entity.bo.AGVRobot;
import com.jimi.agv_mock.thread.RobotInfoReporter;


/**
 * 模拟技田AGV服务器的Mock叉车实时数据
 * <br>
 * <b>2018年7月3日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
@ServerEndpoint("/robotinfo")
public class MockRobotInfoSocket {
	
    private static Session session;
    
	/**
	 * 机器实体集合
	 */
	private static Map<Integer, AGVRobot> robots;
	
	public static RobotInfoReporter reporter;
    
	
    static{
    	robots = new HashMap<>();
		for (Integer id : Constant.ROBOT_IDS) {
			AGVRobot robot = new AGVRobot();
			robot.setRobotid(id);
			robot.setBatteryPower(100);
			robot.setPosX(1);
			robot.setPosY(1);
			robot.setEnable(2);
			robot.setSystem_pause(false);
			robot.setErrorcode(255);
			robot.setWarncode(255);
			robot.setStatus(0);
			robots.put(id, robot);
		}
    }
    
    
    @OnOpen
    public void onOpen(Session session1){
    	session = session1;
    	reporter = new RobotInfoReporter(robots);
    	reporter.start();
    }
    
    
	@OnClose
	public void onClose(Session userSession, CloseReason reason) {
		System.out.println("Client was disconnected2.");
		reporter.interrupt();
	}
    

	public static synchronized void sendMessage(String message) {
        try {
			session.getBasicRemote().sendText(message);
//			System.out.println("["+ new Date().toString() +"]" + "send message:" + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	
	public static Map<Integer, AGVRobot> getRobots() {
		return robots;
	}

}