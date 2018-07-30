package com.jimi.uw_server.agv.handle;

import java.util.List;

import com.jfinal.json.Json;
import com.jimi.uw_server.agv.dao.TaskItemRedisDAO;
import com.jimi.uw_server.agv.entity.cmd.AGVSwitchEnableCmd;
import com.jimi.uw_server.agv.entity.cmd.base.AGVBaseCmd;
import com.jimi.uw_server.agv.socket.AGVMainSocket;

/**
 * 启用、禁用、暂停、继续处理器
 * <br>
 * <b>2018年7月13日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class SwitchHandler {

	public static void sendEnable(List<Integer> robotid) throws Exception {
		sendEnableOrDisable(robotid, true);
	}


	public static void sendDisable(List<Integer> robotid) throws Exception {
		sendEnableOrDisable(robotid, false);
	}
	
	
	public static void sendAllStart() throws Exception {
		sendStartOrPause(true);
	}


	public static void sendAllPause() throws Exception {
		sendStartOrPause(false);
	}


	private static void sendStartOrPause(boolean start) throws Exception {
		AGVBaseCmd cmd = new AGVBaseCmd();
		cmd.setCmdid(TaskItemRedisDAO.getCmdId());
		if(start) {
			cmd.setCmdcode("allstart");
		}else {
			cmd.setCmdcode("allpause");
		}
		AGVMainSocket.sendMessage(Json.getJson().toJson(cmd));
	}


	private static void sendEnableOrDisable(List<Integer> robotid, boolean enabled) throws Exception {
		AGVSwitchEnableCmd cmd = new AGVSwitchEnableCmd();
		cmd.setCmdid(TaskItemRedisDAO.getCmdId());
		cmd.setRobotids(robotid);
		if(enabled) {
			cmd.setCmdcode("enable");
		}else {
			cmd.setCmdcode("disable");
		}
		AGVMainSocket.sendMessage(Json.getJson().toJson(cmd));
	}
}
