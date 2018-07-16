package com.jimi.agv_mock.socket;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.jimi.agv_mock.entity.bo.AGVRobot;


/**
 * 模拟技田AGV服务器的Mock叉车实时数据
 * <br>
 * <b>2018年7月3日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
@ServerEndpoint("/robotinfo")
public class MockRobotInfoSocket {
	
    private Session session;
	
	/**
	 * 机器编号数组
	 */
	private static Map<Integer, AGVRobot> robots = new HashMap<>();

	
    @OnOpen
    public void onOpen(Session session){
    	this.session = session;
    }
    
    
	@OnClose
	public void onClose(Session userSession, CloseReason reason) {
		System.out.println("Client was disconnected2.");
	}
    

	public synchronized void sendMessage(String message) {
        try {
			this.session.getBasicRemote().sendText(message);
			System.out.println("["+ new Date().toString() +"]"+"send message: " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }


	public static Map<Integer, AGVRobot> getRobots() {
		return robots;
	}
	
}