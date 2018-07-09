package com.jimi.agv_mock.socket;

import java.io.IOException;
import java.util.Date;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.fastjson.JSON;
import com.jimi.agv_mock.entity.AGVBaseCmd;
import com.jimi.agv_mock.entity.AGVMoveCmd;
import com.jimi.agv_mock.thread.TaskThread;


/**
 * 模拟技田AGV服务器的Mock服务器
 * <br>
 * <b>2018年7月3日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
@ServerEndpoint("/connect")
public class MockWebSocket {
	
    private Session session;
	private int cmdid;

    
    @OnOpen
    public void onOpen(Session session){
    	this.session = session;
    	sendMessage("AGV 服务当前为Mock模式,服务器类型叉车服务");
    }
    
    
	@OnClose
	public void onClose(Session userSession, CloseReason reason) {
		System.out.println("Client was disconnected.");
	}
    

    @OnMessage
    public void onMessage(String message, Session session) {
    	this.session = session;
    	System.out.println("["+ new Date().toString() +"]" + "receiver message:" + message);
    	
    	//发送ack
		AGVBaseCmd baseCmd;
		try {
			baseCmd = JSON.parseObject(message, AGVBaseCmd.class);
			if(!baseCmd.getCmdcode().equals("ack")) {
				baseCmd.setCmdcode("ack");
				sendMessage(JSON.toJSONString(baseCmd));
			}
		} catch (Exception e) {
		}
		
		//判断是否是ls，sl指令
		if(!message.contains("\"cmdcode\":\"LS\"") && !message.contains("\"cmdcode\":\"SL\"")) {
			return;
		}
		//转换成实体类
		AGVMoveCmd moveCmd = JSON.parseObject(message, AGVMoveCmd.class);
		new TaskThread(this, moveCmd).start();
    }


    public synchronized void sendMessage(String message) {
        try {
			this.session.getBasicRemote().sendText(message);
			System.out.println("["+ new Date().toString() +"]"+"send message: " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
    /**
	 * 获取一个新的CmdId
	 */
	public synchronized int getCmdId() {
		cmdid%=999999;
		cmdid++;
		return cmdid;
	}

}