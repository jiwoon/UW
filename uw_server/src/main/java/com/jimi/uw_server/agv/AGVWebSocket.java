package com.jimi.uw_server.agv;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.jfinal.json.Json;
import com.jimi.uw_server.agv.entity.AGVMissionGroup;
import com.jimi.uw_server.agv.entity.AGVMoveCmd;
import com.jimi.uw_server.agv.entity.AGVStatusCmd;
import com.jimi.uw_server.agv.entity.AGVIOTaskItem;
import com.jimi.uw_server.model.MaterialType;

/**
 * 该类的所有方法由AGVCommunicator调用，请勿在其他地方调用
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
@ClientEndpoint
public class AGVWebSocket {
	
	//指令序列号
	private static int cmdId;
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
		//判断是否是status指令
		if(!message.contains("\"cmdcode\":\"status\"")) {
			return;
		}
		//转换成实体类
		AGVStatusCmd statusCmd = Json.getJson().parse(message, AGVStatusCmd.class);
		//判断是否是第二动作完成
		if(statusCmd.getStatus() != 2) {
			return;
		}
		//获取groupid
		String groupid = statusCmd.getMissiongroupid();
		//查询对应物料类型
		MaterialType materialType = MaterialType.dao.findById(AGVIOTaskItem.fromString(groupid).getMaterialTypeId());
		//判断是LS指令还是SL指令第二动作完成，能在taskitems找到说明是LS，反之是SL
		//LS:
		for (AGVIOTaskItem item : AGVTaskItemSender.getTaskItems()) {
			if(groupid.equals(item.toString())) {
				//移除taskitems里对应item
				AGVTaskItemSender.removeTaskItem(item);
				//构建SL指令，令指定robot把料送回原仓位
				AGVMoveCmd moveCmd = createSLCmd(statusCmd, groupid, materialType, item);
				//发送SL
				sendMessage(Json.getJson().toJson(moveCmd));
				return;
			}
		}
		//SL:
		//更新taskItems对应的物料类型为在架
		materialType.setIsOnShelf(true);
		materialType.update();
		//调用指令发送发方法发送下一条指令
		AGVTaskItemSender.send();
	}


	/**
	 * 使用websocket发送一条消息到AGV服务器
	 */
	public synchronized void sendMessage(String message) {
		session.getAsyncRemote().sendText(message);
	}


	/**
	 * 获取一个新的CmdId
	 */
	public synchronized static int getCmdId() {
		cmdId%=999999;
		cmdId++;
		return cmdId;
	}
	
	
	private AGVMoveCmd createSLCmd(AGVStatusCmd statusCmd, String groupid, MaterialType materialType,
			AGVIOTaskItem item) {
		List<AGVMissionGroup> groups = new ArrayList<>();
		AGVMissionGroup group = new AGVMissionGroup();
		group.setMissiongroupid(groupid);//missionGroupId要和LS指令相同
		group.setRobotid(statusCmd.getRobotid());//robotId要和LS指令相同
		group.setStartx(item.getWindowPositionX());//起点X为仓口X
		group.setStarty(item.getWindowPositionY());//起点Y为仓口Y
		group.setEndx(materialType.getRow());//设置X
		group.setEndy(materialType.getCol());//设置Y
		group.setEndz(materialType.getHeight());//设置Z
		groups.add(group);
		AGVMoveCmd moveCmd = new AGVMoveCmd();
		moveCmd.setCmdcode("SL");
		moveCmd.setCmdid(getCmdId());
		moveCmd.setMissiongroups(groups);
		return moveCmd;
	}
}