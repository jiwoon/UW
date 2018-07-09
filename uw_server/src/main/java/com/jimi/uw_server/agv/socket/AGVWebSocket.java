package com.jimi.uw_server.agv.socket;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
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
import com.jimi.uw_server.agv.dao.AGVTaskItemRedisDAO;
import com.jimi.uw_server.agv.entity.AGVBaseCmd;
import com.jimi.uw_server.agv.entity.AGVIOTaskItem;
import com.jimi.uw_server.agv.entity.AGVMissionGroup;
import com.jimi.uw_server.agv.entity.AGVMoveCmd;
import com.jimi.uw_server.agv.entity.AGVStatusCmd;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.model.Robot;
import com.jimi.uw_server.model.Task;
import com.jimi.uw_server.util.ErrorLogWritter;

/**
 * 与AGV服务器进行通讯的类
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
			//调用指令发送方法，下达数目与有效机器数相等的指令 
			sendIOCmd();
		} catch (Exception e) {
			ErrorLogWritter.save(e.getClass().getSimpleName() + ":" + e.getMessage());
			e.printStackTrace();
		}
	}


	@OnClose
	public void onClose(Session userSession, CloseReason reason) {
		System.out.println("AGVCommunicator was Stopped.");
	}

	
	@OnMessage
	public void onMessage(String message) {
		try {
			System.err.println("["+ new Date().toString() +"]" + "receiver message:" + message);
			
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
			
			//判断是否是第二动作完成
			if(statusCmd.getStatus() != 2) {
				return;
			}
			
			//获取groupid
			String groupid = statusCmd.getMissiongroupid();
			
			//查询对应物料类型
			MaterialType materialType = MaterialType.dao.findById(groupid.split(":")[0]);
			
			//判断是LS指令还是SL指令第二动作完成，能在taskitems找到说明是LS，反之是SL
			//LS:
			for (AGVIOTaskItem item : AGVTaskItemRedisDAO.getTaskItems()) {
				if(groupid.equals(item.toString().split("#")[0]) && item.getState() == 1) {
					//更改taskitems里对应item状态为2（已拣料到站）
					AGVTaskItemRedisDAO.updateTaskItemState(item, 2);
					//构建SL指令，令指定robot把料送回原仓位
					AGVMoveCmd moveCmd = createSLCmd(statusCmd, groupid, materialType, item);
					//发送SL
					sendMessage(Json.getJson().toJson(moveCmd));
					return;
				}
			}
			
			//SL:
			//在数据库标记所有处于该坐标的物料为在架
			List<MaterialType> specifiedPositionMaterialTypes = MaterialType.dao.find(SPECIFIED_POSITION_MATERIAL_TYEP_SQL,
					materialType.getRow(), materialType.getCol(), materialType.getHeight());
			for (MaterialType mt: specifiedPositionMaterialTypes) {
				mt.setIsOnShelf(true);
				mt.update();
			}
			
			/*
			 * 判断该groupid所在的任务是否全部条目状态为2（已拣料到站），如果是，则清除所有该任务id对应的条目，
			 * 释放内存，并修改数据库任务状态
			*/
			boolean isAllFinish = true;
			for (AGVIOTaskItem item : AGVTaskItemRedisDAO.getTaskItems()) {
				if(groupid.split(":")[3].equals(item.toString().split("#")[0].split(":")[3]) && item.getState() != 2) {
					isAllFinish = false;
				}
			}
			if(isAllFinish) {
				int taskId = Integer.valueOf(groupid.split(":")[3]);
				AGVTaskItemRedisDAO.removeTaskItemByTaskId(taskId);
				Task task = new Task();
				task.setId(taskId);
				task.setState(3);
				task.update();
			}
			
			//调用指令发送发方法发送下一条指令
			sendIOCmd();
		} catch (Exception e) {
			ErrorLogWritter.save(e.getClass().getSimpleName()+ ":" +e.getMessage());
			e.printStackTrace();
		}
	}


	/**
		 * 发送出入库条目指令
		 */
		public void sendIOCmd() {
			//判断til是否为空
			List<AGVIOTaskItem> taskItems = new ArrayList<>();
			AGVTaskItemRedisDAO.appendTaskItems(taskItems);
			if (taskItems.isEmpty()) {
				AGVTaskItemRedisDAO.setLcn(0);
				return;
			}
			//统计当前有效robot数目赋值到cn
			int cn = Robot.dao.find(ENABLED_ROBOT_SQL, 1).size();
			int lcn = Integer.valueOf(AGVTaskItemRedisDAO.getLcn());
			if (lcn > cn - 1) {
				lcn = cn - 1;
				AGVTaskItemRedisDAO.setLcn(lcn);
				return;
			}
			int b = cn;
			cn = cn - lcn;
			lcn = b - 1;
			int a = 0;
			AGVTaskItemRedisDAO.setLcn(lcn);
			//根据materialType表生成物料是否在架情况映射mtc
			Map<Integer, MaterialType> mtc = new HashMap<>();
			for (AGVIOTaskItem item : taskItems) {
				mtc.put(item.getMaterialTypeId(), null);
			}
			StringBuffer sb = new StringBuffer(SPECIFIED_ID_MATERIAL_TYPE_SQL);
			for (int i = 0; i < mtc.size(); i++) {
				sb.insert(sb.indexOf(")"), "?,");
			}
			sb.delete(sb.lastIndexOf(","), sb.lastIndexOf(",") + 1);
			List<MaterialType> materialTypes = MaterialType.dao.find(sb.toString(), mtc.keySet().toArray());
			for (MaterialType materialType : materialTypes) {
				mtc.put(materialType.getId(), materialType);
			}
			//获取第a个元素
			while(cn != 0 && a != taskItems.size()) {
				AGVIOTaskItem item = taskItems.get(a);
				MaterialType materialType = mtc.get(item.getMaterialTypeId());
				//判断是否在架并且状态是否为0（未分配）
				if (materialType.getIsOnShelf() && item.getState() == 0) {
					//在mtc内标记所有处于该坐标的物料不在架
					for (MaterialType mt : mtc.values()) {
						if(materialType.getRow() == mt.getRow() && 
								materialType.getCol() == mt.getCol() && 
								materialType.getHeight() == mt.getHeight()) {
							mt.setIsOnShelf(false);
						}
					}
					//在数据库标记所有处于该坐标的物料为不在架
					List<MaterialType> specifiedPositionMaterialTypes = MaterialType.dao.find(SPECIFIED_POSITION_MATERIAL_TYEP_SQL,
							materialType.getRow(), materialType.getCol(), materialType.getHeight());
					for (MaterialType mt: specifiedPositionMaterialTypes) {
						mt.setIsOnShelf(false);
						mt.update();
					}
					//发送LS
					AGVMoveCmd cmd = createLSCmd(materialType, item);
					sendMessage(Json.getJson().toJson(cmd));
					//更新任务条目状态为已分配
					AGVTaskItemRedisDAO.updateTaskItemState(item, 1);
					cn--;
				}
				a++;
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
		moveCmd.setCmdid(AGVTaskItemRedisDAO.getCmdId());
		moveCmd.setMissiongroups(groups);
		return moveCmd;
	}


	private AGVMoveCmd createLSCmd(MaterialType materialType, AGVIOTaskItem item) {
		AGVMissionGroup group = new AGVMissionGroup();
		group.setMissiongroupid(item.toString().split("#")[0]);
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
		cmd.setCmdid(AGVTaskItemRedisDAO.getCmdId());
		cmd.setMissiongroups(groups);
		return cmd;
	}
}