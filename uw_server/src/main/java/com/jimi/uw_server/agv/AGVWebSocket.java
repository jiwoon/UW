package com.jimi.uw_server.agv;

import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import com.jimi.uw_server.agv.entity.AGVBaseCmd;
import com.jimi.uw_server.agv.entity.AGVIOTaskItem;
import com.jimi.uw_server.agv.entity.AGVMissionGroup;
import com.jimi.uw_server.agv.entity.AGVMoveCmd;
import com.jimi.uw_server.agv.entity.AGVStatusCmd;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.model.Robot;
import com.jimi.uw_server.util.ErrorLogWritter;

/**
 * 该类的所有方法由AGVCommunicator调用，请勿在其他地方调用
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
@ClientEndpoint
public class AGVWebSocket {
	
	public static AGVWebSocket me;
	
	private static final String ENABLED_ROBOT_SQL = "SELECT * FROM robot WHERE enabled = ?";
	private static final String SPECIFIED_ID_MATERIAL_TYPE_SQL = "SELECT * FROM material_type WHERE id IN()";
	private static final String SPECIFIED_POSITION_MATERIAL_TYEP_SQL = "SELECT * FROM material_type WHERE row = ? AND col = ? AND height = ?";
	
	//指令序列号
	private int cmdId;
	//会话
	private Session session;
	
	
	public static void connect(String uri) {
		try {
			//连接AGV服务器
			me = new AGVWebSocket(new URI(uri));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	@OnOpen
	public void onOpen(Session userSession) {
		try {
			session = userSession;
			System.out.println("AGVCommunicator is Running Now...");
//			cancelTest();
//			getLocationTest();
//			getStationTest();
//			LLTest();
//			LLTest2();
//			SLTest();
//			LSTest();
			//调用指令发送方法，下达数目与有效机器数相等的指令 
			sendIOCmd();
		} catch (Exception e) {
			ErrorLogWritter.save(e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	private void LSTest() {
		int id = new Random().nextInt() % 1000;
		
		AGVMoveCmd moveCmd = new AGVMoveCmd();
		List<AGVMissionGroup> groups = new ArrayList<>();
		AGVMissionGroup group = new AGVMissionGroup();
		group.setMissiongroupid(id + "");
		group.setRobotid(3111);
		group.setStartx(16);
		group.setStarty(11);
		group.setStartz(1);
		group.setEndx(18);
		group.setEndy(4);
		groups.add(group);
		moveCmd.setCmdcode("LS");
		moveCmd.setCmdid(getCmdId());
		moveCmd.setMissiongroups(groups);
		sendMessage(Json.getJson().toJson(moveCmd));
	}


	private void getLocationTest() {
		AGVBaseCmd baseCmd = new AGVBaseCmd();
		baseCmd.setCmdcode("getlocations");
		baseCmd.setCmdid(getCmdId());
		sendMessage(Json.getJson().toJson(baseCmd));
	}
	
	private void getStationTest() {
		AGVBaseCmd baseCmd = new AGVBaseCmd();
		baseCmd.setCmdcode("getstations");
		baseCmd.setCmdid(getCmdId());
		sendMessage(Json.getJson().toJson(baseCmd));
	}
	
	private void LLTest() {
		//构建SL指令，令指定robot把料送回原仓位
		AGVMoveCmd moveCmd = new AGVMoveCmd();
		List<AGVMissionGroup> groups = new ArrayList<>();
		AGVMissionGroup group = new AGVMissionGroup();
		group.setMissiongroupid("test");
		group.setRobotid(0);
		group.setStartx(16);
		group.setStarty(11);
		group.setStartz(1);
		group.setEndx(16);
		group.setEndy(8);
		group.setEndz(2);
		groups.add(group);
		moveCmd.setCmdcode("LL");
		moveCmd.setCmdid(getCmdId());
		moveCmd.setMissiongroups(groups);
		//发送LL
		sendMessage(Json.getJson().toJson(moveCmd));
	}
	
	private void LLTest2() {
		//构建SL指令，令指定robot把料送回原仓位
		AGVMoveCmd moveCmd = new AGVMoveCmd();
		List<AGVMissionGroup> groups = new ArrayList<>();
		AGVMissionGroup group = new AGVMissionGroup();
		group.setMissiongroupid("test");
		group.setRobotid(0);
		group.setStartx(16);
		group.setStarty(8);
		group.setStartz(2);
		group.setEndx(16);
		group.setEndy(11);
		group.setEndz(1);
		groups.add(group);
		moveCmd.setCmdcode("LL");
		moveCmd.setCmdid(getCmdId());
		moveCmd.setMissiongroups(groups);
		//发送LL
		sendMessage(Json.getJson().toJson(moveCmd));
	}
	
	
//	private void cancelTest() {
//		for (int i = 0; i < 1000; i++) {
//			AGVCancelCmd cancelCmd = new AGVCancelCmd();
//			cancelCmd.setCmdcode("cancel");
//			cancelCmd.setCmdid(getCmdId());
//			cancelCmd.setMissiongroupid(i+"");
//			//发送Cancel
//			sendMessage(Json.getJson().toJson(cancelCmd));
//		}
//	}


	@OnClose
	public void onClose(Session userSession, CloseReason reason) {
		System.out.println("AGVCommunicator was Stopped.");
	}

	
	@OnMessage
	public void onMessage(String message) {
		try {
			System.out.println("["+ new Date().toString() +"]" + "receiver message:" + message);
			
			//发送ack
			AGVBaseCmd baseCmd;
			try {
				baseCmd = Json.getJson().parse(message, AGVBaseCmd.class);
				if(!baseCmd.getCmdcode().equals("ack")) {
					baseCmd.setCmdcode("ack");
					sendMessage(Json.getJson().toJson(baseCmd));
				}
			} catch (Exception e) {
			}
			
			//判断是否是status指令
			if(!message.contains("\"cmdcode\":\"status\"")) {
				return;
			}
			//转换成实体类
			AGVStatusCmd statusCmd = Json.getJson().parse(message, AGVStatusCmd.class);
			
//			//判断是否是LS第二动作完成
//			if(statusCmd.getStatus() != 2) {
//				return;
//			}
//			
//			
//			AGVMoveCmd moveCmd2 = new AGVMoveCmd();
//			List<AGVMissionGroup> groups2 = new ArrayList<>();
//			AGVMissionGroup group2 = new AGVMissionGroup();
//			group2.setMissiongroupid(statusCmd.getMissiongroupid());
//			group2.setRobotid(statusCmd.getRobotid());
//			group2.setStartx(18);
//			group2.setStarty(4);
//			group2.setEndx(16);
//			group2.setEndy(11);
//			group2.setEndz(1);
//			groups2.add(group2);
//			moveCmd2.setCmdcode("SL");
//			moveCmd2.setCmdid(getCmdId());
//			moveCmd2.setMissiongroups(groups2);
//			sendMessage(Json.getJson().toJson(moveCmd2));
			
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
		} catch (Exception e) {
			ErrorLogWritter.save(e.getMessage());
			e.printStackTrace();
		}
	}


	/**
	 * 使用websocket发送一条消息到AGV服务器
	 */
	private void sendMessage(String message) {
		System.out.println("["+ new Date().toString() +"]"+"send message: " + message);
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
	public void sendIOCmd() {
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
		//根据materialType表生成物料是否在架情况映射mtc
		Map<Integer, MaterialType> mtc = new HashMap<>();
		StringBuffer sb = new StringBuffer(SPECIFIED_ID_MATERIAL_TYPE_SQL);
		for (AGVIOTaskItem item : taskItems) {
			mtc.put(item.getMaterialTypeId(), null);
			sb.insert(sb.indexOf(")"), "?,");
		}
		sb.delete(sb.lastIndexOf(","), sb.lastIndexOf(",") + 1);
		List<MaterialType> materialTypes = MaterialType.dao.find(sb.toString(), mtc.keySet().toArray());
		for (MaterialType materialType : materialTypes) {
			mtc.put(materialType.getId(), materialType);
		}
		//获取第a个元素
		while(cn != 0) {
			AGVIOTaskItem item = taskItems.get(a);
			MaterialType materialType = mtc.get(item.getMaterialTypeId());
			//判断是否在架
			if (materialType.getIsOnShelf()) {
				//获取所有和该物料的坐标相同的物料
				List<MaterialType> specifiedPositionMaterialTypes = MaterialType.dao.find(SPECIFIED_POSITION_MATERIAL_TYEP_SQL,
						materialType.getRow(), materialType.getCol(), materialType.getHeight());
				for (MaterialType mt: specifiedPositionMaterialTypes) {
					//标记所有处于该坐标的物料为不在架
					mt.setId(item.getMaterialTypeId());
					mt.setIsOnShelf(false);
					mt.update();
					mtc.put(item.getMaterialTypeId(), mt);
				}
				//发送LS
				AGVMoveCmd cmd = createLSCmd(materialType, item);
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


	private AGVMoveCmd createLSCmd(MaterialType materialType, AGVIOTaskItem item) {
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