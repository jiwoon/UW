package com.jimi.uw_server.agv;

import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.jfinal.json.Json;
import com.jimi.uw_server.agv.entity.AGVMoveCmd;

/**
 * 该类的所有方法由AGVCommunicator调用，请勿在其他地方调用
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
@ClientEndpoint
public class AGVWebSocket {
	
	//指令序列号
	private int cmdId;
	//会话
	private Session session;
	
	public AGVWebSocket(URI endpointURI) throws Exception {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		session = container.connectToServer(this, endpointURI);
	}


	@OnOpen
	public void onOpen(Session userSession) {
		System.out.println("AGVCommunicator is Running Now...");
		//调用指令发送方法，下达数目与有效机器数相等的指令
		AGVTaskItemSender.send();
	}

	@OnClose
	public void onClose(Session userSession, CloseReason reason) {
		System.out.println("AGVCommunicator was Stopped.");
	}

	
	@OnMessage
	public void onMessage(String message) {
		System.out.println("receiver message:" + message);
	}
	
	
	private int getCmdId() {
		cmdId%=999999;
		cmdId++;
		return cmdId;
	}
	
	
	public void sendMessage(String message) {
		
	}

}