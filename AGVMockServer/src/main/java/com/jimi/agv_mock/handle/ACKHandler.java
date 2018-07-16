package com.jimi.agv_mock.handle;

import com.alibaba.fastjson.JSON;
import com.jimi.agv_mock.entity.cmd.base.AGVBaseCmd;
import com.jimi.agv_mock.socket.MockMainSocket;

/**
 * ACK
 * <br>
 * <b>2018年7月10日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class ACKHandler {

	/**
	 * 处理ACK指令
	 */
	public static void handleACK(String message) {
		//在Map修改该CMDID对应的值为已收到ACK
		AGVBaseCmd baseCmd = JSON.parseObject(message, AGVBaseCmd.class);
		int cmdid = baseCmd.getCmdid();
		MockMainSocket.getSendCmdidAckMap().put(cmdid, true);
	}

	/**
	 * 处理非ACK指令
	 */
	public static void handleNOTACK(String message) {
		AGVBaseCmd baseCmd = JSON.parseObject(message, AGVBaseCmd.class);
		//判断是否已经ack过
		for (Integer cmdid : MockMainSocket.getReceiveNotAckCmdidSet()) {
			if(baseCmd.getCmdid() == cmdid) {
				return;
			}
		}
		//发送ack
		baseCmd.setCmdcode("ack");
		MockMainSocket.sendACK(JSON.toJSONString(baseCmd));
		//添加到已经ACK的非ACK指令CMDID集合
		MockMainSocket.getReceiveNotAckCmdidSet().add(baseCmd.getCmdid());
	}
}
	



