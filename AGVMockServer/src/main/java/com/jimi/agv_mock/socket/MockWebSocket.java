package com.jimi.agv_mock.socket;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.fastjson.JSON;
import com.jimi.agv_mock.constant.Constant;
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

	/**
	 * 发送的CMDID与是否被ACK的关系映射
	 */
	private Map<Integer, Boolean> sendCmdidAckMap;
    
	/**
	 * 已收到的非ACK的CMDID集合
	 */
	private Set<Integer> receiveNotAckCmdidSet;
	
    @OnOpen
    public void onOpen(Session session){
    	this.session = session;
    	sendCmdidAckMap = new HashMap<>();
    	receiveNotAckCmdidSet = new HashSet<>();
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
    	
		AGVBaseCmd baseCmd;
		try {
			baseCmd = JSON.parseObject(message, AGVBaseCmd.class);
			if(!baseCmd.getCmdcode().equals("ack")) {//如果不是ACK消息
				//判断是否已经ack过
				for (Integer cmdid : receiveNotAckCmdidSet) {
					if(baseCmd.getCmdid() == cmdid) {
						return;
					}
				}
				
				//模拟延迟
				Thread.sleep(Constant.WAIT_ACK_TIMEOUT + new Random().nextInt() % 500);
				//回复ACK
				baseCmd.setCmdcode("ack");
				sendMessage(JSON.toJSONString(baseCmd));
				receiveNotAckCmdidSet.add(baseCmd.getCmdid());
				
				//交给对应命令处理器：
				//判断是否是ls，sl指令
				if(message.contains("\"cmdcode\":\"LS\"") || message.contains("\"cmdcode\":\"SL\"")) {
					handleLSSL(message);
				}
			}else {//如果是ACK消息
				//在Map修改该CMDID对应的值为已收到ACK
				int cmdid = baseCmd.getCmdid();
				sendCmdidAckMap.put(cmdid, true);
			}
		} catch (Exception e) {
			//防止一些不合格式的json
			//...
		}
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


	private void handleLSSL(String message) {
		//转换成实体类
		AGVMoveCmd moveCmd = JSON.parseObject(message, AGVMoveCmd.class);
		//启动叉车任务线程
		new TaskThread(this, moveCmd).start();
	}


	public Map<Integer, Boolean> getSendCmdidAckMap() {
		return sendCmdidAckMap;
	}

}