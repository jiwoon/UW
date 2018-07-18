package com.jimi.uw_server.agv.socket;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	
	private static final String GET_ALL_SQL = "SELECT * FROM robot";
	
	private static String uri;
	
	/**
	 * 机器实体集合
	 */
	private static Map<Integer, AGVRobot> robots;
	
	
	
	
	public static void init(String uri) {
		try {
			robots = new HashMap<>();
			//从数据库获取叉车数据
			for (Robot robot : Robot.dao.find(GET_ALL_SQL)) {
				AGVRobot agvRobot = AGVRobot.fromModel(robot);
				robots.put(agvRobot.getRobotid(), agvRobot);
			}
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
			
			//获取新的机器数据
			Map<Integer, AGVRobot> newRobots = new HashMap<>();
			AGVRobotInfoCmd robotInfoCmd = Json.getJson().parse(message, AGVRobotInfoCmd.class);
			for (AGVRobot agvRobot : robotInfoCmd.getRobotarray()) {
				newRobots.put(agvRobot.getRobotid(), agvRobot);
			}
			
			//获取新增项
			Set<Integer> addRobotsIds = new HashSet<>(newRobots.keySet());
			addRobotsIds.removeAll(robots.keySet());
			//新增机器记录
			for (Integer id : addRobotsIds) {
				Robot robot = AGVRobot.toModel(newRobots.get(id));
				robot.save();
			}
			
			//获取减少项
			Set<Integer> removeRobotsIds = new HashSet<>(robots.keySet());
			removeRobotsIds.removeAll(newRobots.keySet());
			//删除机器记录
			for (Integer id : removeRobotsIds) {
				Robot.dao.deleteById(id);
			}
			
			//获取修改项
			Set<Integer> modifyRobotsIds = new HashSet<>(newRobots.keySet());
			modifyRobotsIds.retainAll(robots.keySet());
			//修改机器记录
			for (Integer id : modifyRobotsIds) {
				Robot robot = AGVRobot.toModel(newRobots.get(id));
				robot.update();
			}
			
			robots = newRobots;
			
		}catch (Exception e) {
			ErrorLogWritter.save(e.getClass().getSimpleName() + ":" + e.getMessage());
			e.printStackTrace();
		}
	}

	
	private static void connect(String uri) throws Exception {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		container.connectToServer(new RobotInfoSocket(), new URI(uri));
	}

	
	public static Map<Integer, AGVRobot> getRobots() {
		return robots;
	}
	
}