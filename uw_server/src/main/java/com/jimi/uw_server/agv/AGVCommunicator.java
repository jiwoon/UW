package com.jimi.uw_server.agv;

import java.net.URI;

import com.alibaba.fastjson.JSON;
import com.jimi.uw_server.agv.entity.AGVMoveCmd;

/**
 * AGV通讯器工具类 <br>
 * <b>2018年6月15日</b>
 * 
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AGVCommunicator {

	private static AGVWebSocket webSocket;

	
	public static void connect(String uri) {
		try {
			//连接AGV服务器
			webSocket = new AGVWebSocket(new URI(uri));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 调用该接口，向AGV发送LS、LL、SS、SL等指令
	 */
	public static void sendMoveCmd(AGVMoveCmd cmd) {
		webSocket.sendMessage(JSON.toJSONString(cmd));
	}
	
}