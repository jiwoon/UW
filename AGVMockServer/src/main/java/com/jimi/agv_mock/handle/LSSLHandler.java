package com.jimi.agv_mock.handle;

import com.alibaba.fastjson.JSON;
import com.jimi.agv_mock.entity.cmd.AGVMoveCmd;
import com.jimi.agv_mock.socket.MockMainSocket;

/**
 * LSSL处理器
 * <br>
 * <b>2018年7月13日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class LSSLHandler {

	
	public static void handleLSSL(String message) {
		//转换成实体类
		AGVMoveCmd moveCmd = JSON.parseObject(message, AGVMoveCmd.class);
		//判断类型
		if(moveCmd.getCmdcode().equals("LS")) {
			handleLS(moveCmd);
		}else {
			handleSL(moveCmd);
		}
		
	}
	
	
	private static void handleLS(AGVMoveCmd cmd) {
		//放入任务池
		MockMainSocket.getTaskPool().addLSTask(cmd);
	}

	
	private static void handleSL(AGVMoveCmd cmd) {
		//放入任务池
		MockMainSocket.getTaskPool().addSLTask(cmd);
	}
}
