package com.jimi.uw_server.agv;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.jfinal.json.Json;
import com.jfinal.plugin.redis.Redis;
import com.jimi.uw_server.agv.entity.AGVMissionGroup;
import com.jimi.uw_server.agv.entity.AGVMoveCmd;
import com.jimi.uw_server.agv.entity.AGVStatusCmd;
import com.jimi.uw_server.agv.entity.AGVIOTaskItem;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.model.Robot;

/**
 * 该类的所有方法由AGVCommunicator调用，请勿在其他地方调用
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
@ClientEndpoint
public class AGVWebSocket {
	
	private static final String ENABLED_ROBOT_SQL = "SELECT * FROM robot WHERE enabled = ?";
	private static final String SPECIFIED_ID_MATERIAL_TYPE_SQL = "SELECT * FROM material_type WHERE id IN()";
	
	//指令序列号
	private int cmdId;
	//会话
	private Session session;
	
	
	public static void connect(String uri) {
		try {
			//连接AGV服务器
			new AGVWebSocket(new URI(uri));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	@OnOpen
	public void onOpen(Session userSession) {
		session = userSession;
		System.out.println("AGVCommunicator is Running Now...");
		//调用指令发送方法，下达数目与有效机器数相等的指令
		sendIOCmd();
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
		for (AGVIOTaskItem item : AGVTaskItemRedisDAO.getTaskItems()) {
			if(groupid.equals(item.toString())) {
				//移除taskitems里对应item
				AGVTaskItemRedisDAO.removeTaskItem(item);
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
		sendIOCmd();
	}


	/**
	 * 使用websocket发送一条消息到AGV服务器
	 */
	private void sendMessage(String message) {
		session.getAsyncRemote().sendText(message);
	}


	private AGVWebSocket(URI endpointURI) throws Exception {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		container.connectToServer(this, endpointURI);
	}


	/**
	 * 获取一个新的CmdId
	 */
	private synchronized int getCmdId() {
		cmdId%=999999;
		cmdId++;
		return cmdId;
	}
	
	
	/**
	 * 发送出入库条目指令
	 */
	private void sendIOCmd() {
		//判断til是否为空
		List<AGVIOTaskItem> taskItems = new ArrayList<>();
		AGVTaskItemRedisDAO.appendTaskItems(taskItems);
		if (taskItems.isEmpty()) {
			return;
		}
		//统计当前有效robot数目赋值到cn
		int cn = Robot.dao.find(ENABLED_ROBOT_SQL, 1).size();
		int lcn = Integer.valueOf(Redis.use().getJedis().get("lcn"));
		if (lcn > cn - 1) {
			lcn = cn - 1;
			Redis.use().set("lcn", lcn);
			return;
		}
		int b = cn;
		cn = cn - lcn;
		lcn = b - 1;
		int a = 0;
		Redis.use().getJedis().set("lcn", lcn + "");
		//根据materialType表生成物料是否在架情况映射msm
		Map<Integer, Boolean> msm = new HashMap<>();
		StringBuffer sb = new StringBuffer(SPECIFIED_ID_MATERIAL_TYPE_SQL);
		for (AGVIOTaskItem item : taskItems) {
			msm.put(item.getMaterialTypeId(), null);
			sb.insert(sb.indexOf(")"), "?,");
		}
		sb.delete(sb.lastIndexOf(","), sb.lastIndexOf(",") + 1);
		List<MaterialType> materialTypes = MaterialType.dao.find(sb.toString(), msm.keySet().toArray());
		for (MaterialType materialType : materialTypes) {
			msm.put(materialType.getId(), materialType.getIsOnShelf());
		}
		//获取第a个元素
		while(cn != 0) {
			AGVIOTaskItem item = taskItems.get(a);
			//判断是否在架
			if (msm.get(item.getMaterialTypeId()) == true) {
				//标记为不在架
				MaterialType materialType = MaterialType.dao.findById(item.getMaterialTypeId());
				materialType.setIsOnShelf(false);
				materialType.update();
				//发送LS
				AGVMoveCmd cmd = createLSCmd(item);
				sendMessage(Json.getJson().toJson(cmd));
				a = 0;
				cn--;
			} else {
				a++;
				a %= taskItems.size();
			}
		}
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


	private AGVMoveCmd createLSCmd(AGVIOTaskItem item) {
		MaterialType materialType = MaterialType.dao.findById(item.getMaterialTypeId());//查询对应物料类型
		AGVMissionGroup group = new AGVMissionGroup();
		group.setMissiongroupid(item.toString());
		group.setRobotid(0);//让AGV系统自动分配
		group.setStartx(materialType.getRow());//物料Row
		group.setStarty(materialType.getCol());//物料Col
		group.setStartz(materialType.getHeight());//物料Height
		group.setEndx(item.getWindowPositionX());//仓口X
		group.setEndy(item.getWindowPositionY());//仓口Y
		List<AGVMissionGroup> groups = new ArrayList<>();
		groups.add(group);
		AGVMoveCmd cmd = new AGVMoveCmd();
		cmd.setCmdcode("LS");
		cmd.setCmdid(getCmdId());
		cmd.setMissiongroups(groups);
		return cmd;
	}
}