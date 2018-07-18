package com.jimi.uw_server.agv.socket;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.jfinal.json.Json;
import com.jimi.uw_server.agv.entity.bo.AGVRobot;
import com.jimi.uw_server.agv.entity.cmd.AGVRobotInfoCmd;
import com.jimi.uw_server.model.Robot;
import com.jimi.uw_server.util.ErrorLogWritter;

/**
 * 实时接收机器信息的类
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
@ClientEndpoint
public class RobotInfoSocket{
	
	private static String uri;
	
	/**
	 * 机器实体集合
	 */
	private static Map<Integer, Robot> robots;
	
	
	public static void init(String uri) {
		try {
			robots = new HashMap<>();
			//连接AGV服务器
			RobotInfoSocket.uri = uri;
			connect(uri);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	@OnOpen
	public void onOpen(Session userSession) {
		System.out.println("RobotInfoSocket is Running Now...");
		try {
		} catch (Exception e) {
			ErrorLogWritter.save(e.getClass().getSimpleName() + ":" + e.getMessage());
			e.printStackTrace();
		}
	}


	@OnClose
	public void onClose(Session userSession, CloseReason reason) {
		ErrorLogWritter.save("RobotInfoSocket was Stopped because :" + reason.getReasonPhrase());
		try {
			Thread.sleep(3000);
			//重新连接
			connect(uri);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@OnMessage
	public void onMessage(String message ,Session session) {
		try {
			System.out.println("["+ new Date().toString() +"]" + "receiver message:" + message);
			AGVRobotInfoCmd robotInfoCmd = Json.getJson().parse(message, AGVRobotInfoCmd.class);
			for (AGVRobot agvRobot : robotInfoCmd.getRobotarry()) {
				Integer robotid = agvRobot.getRobotid();
				Robot robot = Robot.dao.findById(robotid);
				robot.setBattery(agvRobot.getBatteryPower());
				robot.setEnabled(agvRobot.getEnable());
				robot.setError(agvRobot.getErrorcode());
				robot.setWarn(agvRobot.getWarncode());
				robot.setX(agvRobot.getPosX());
				robot.setY(agvRobot.getPosY());
				robot.setStatus(agvRobot.getStatus());
				robot.setPause(agvRobot.getSystem_pause());
				robots.put(robotid, robot);
				robot.update();
			}
		}catch (Exception e) {
			ErrorLogWritter.save(e.getClass().getSimpleName() + ":" + e.getMessage());
			e.printStackTrace();
		}
	}


	private static void connect(String uri) throws Exception {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		container.connectToServer(new RobotInfoSocket(), new URI(uri));
	}

	
	public static Map<Integer, Robot> getRobots() {
		return robots;
	}
	
}