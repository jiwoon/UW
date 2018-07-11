package com.jimi.uw_server.agv.socket;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.log4j.Logger;

import com.jfinal.json.Json;
import com.jfinal.kit.PropKit;
import com.jimi.uw_server.agv.entity.AGVBaseCmd;
import com.jimi.uw_server.agv.handle.ACKHandler;
import com.jimi.uw_server.agv.handle.LSSLHandler;
import com.jimi.uw_server.util.ErrorLogWritter;

/**
 * 与AGV服务器进行通讯的类
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
@ClientEndpoint
public class AGVWebSocket {
	
	private final static Logger logger = Logger.getLogger("");
	
	private static final long WAIT_ACK_TIMEOUT = 3000;

	private static Session session;
	
	/**
	 * 发送的CMDID与是否被ACK的关系映射
	 */
	private static Map<Integer, Boolean> cmdidAckMap;
	/**
	 * 已收到的非ACK的CMDID集合
	 */
	private static Set<Integer> receiveNotAckCmdidSet;
	
	
	public static void connect(String uri) {
		try {
			//连接AGV服务器
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			container.connectToServer(new AGVWebSocket(), new URI(uri));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	@OnOpen
	public void onOpen(Session userSession) {
		logger.info("AGVCommunicator is Running Now...");
		session = userSession;
		cmdidAckMap = new HashMap<>();
		receiveNotAckCmdidSet = new HashSet<>();
		try {
			LSSLHandler.sendLS();
		} catch (Exception e) {
			ErrorLogWritter.save(e.getClass().getSimpleName() + ":" + e.getMessage());
			e.printStackTrace();
		}
	}


	@OnClose
	public void onClose(Session userSession, CloseReason reason) {
		logger.info("AGVCommunicator was Stopped because :" + reason.getReasonPhrase());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//重新连接
		connect(PropKit.use("properties.ini").get("agvServerURI"));
	}

	
	@OnMessage
	public void onMessage(String message ,Session session) {
		AGVWebSocket.session = session;
		try {
			System.err.println("["+ new Date().toString() +"]" + "receiver message:" + message);
			
			//判断是否是ack指令
			if(message.contains("\"cmdcode\":\"ack\"")) {//ack指令
				ACKHandler.handleACK(message);
			}else if(message.contains("\"cmdcode\"")){//非ack指令
				ACKHandler.handleNOTACK(message);
				
				//判断是否是status指令
				if(message.contains("\"cmdcode\":\"status\"")) {
					LSSLHandler.handleStatus(message);
				}
			}
			
		} catch (Exception e) {
			ErrorLogWritter.save(e.getClass().getSimpleName()+ ":" +e.getMessage());
			e.printStackTrace();
		}
	}

	
	/**
	 * 使用websocket发送一条ACK到AGV服务器
	 */
	public static void sendACK(String message) {
		int cmdid = Json.getJson().parse(message, AGVBaseCmd.class).getCmdid();
		Thread thread = new Thread("CMDID-" + cmdid) {
			public void run() {
				try {
					//模拟延迟
					sleep(WAIT_ACK_TIMEOUT + new Random().nextInt() % 500);
					send(message);
				} catch (Exception e) {
					ErrorLogWritter.save(e.getClass().getSimpleName()+ ":" +e.getMessage());
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	

	/**
	 * 使用websocket发送一条消息到AGV服务器
	 */
	public static void sendMessage(String message) {
		int cmdid = Json.getJson().parse(message, AGVBaseCmd.class).getCmdid();
		Thread thread = new Thread("CMDID-" + cmdid) {
			public void run() {
				try {
					send(message);
					cmdidAckMap.put(cmdid, false);
					Thread.sleep(WAIT_ACK_TIMEOUT);
					while(!cmdidAckMap.get(cmdid)) {
						send(message);
						Thread.sleep(WAIT_ACK_TIMEOUT);
					}
				} catch (Exception e) {
					ErrorLogWritter.save(e.getClass().getSimpleName()+ ":" +e.getMessage());
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	
	public static Map<Integer, Boolean> getCmdidAckMap(){
		return cmdidAckMap;
	}


	public static Set<Integer> getReceiveNotAckCmdidSet() {
		return receiveNotAckCmdidSet;
	}


	private static void send(String message) throws IOException {
		synchronized (AGVWebSocket.class) {
			logger.info("["+ new Date().toString() +"]"+"send message: " + message);
			session.getBasicRemote().sendText(message);
		}
	}
	
}