package com.jimi.uw_server.agv.handle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.json.Json;
import com.jimi.uw_server.agv.dao.TaskItemRedisDAO;
import com.jimi.uw_server.agv.entity.bo.AGVIOTaskItem;
import com.jimi.uw_server.agv.entity.bo.AGVMissionGroup;
import com.jimi.uw_server.agv.entity.cmd.AGVMoveCmd;
import com.jimi.uw_server.agv.entity.cmd.AGVStatusCmd;
import com.jimi.uw_server.agv.socket.AGVMainSocket;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.model.Robot;
import com.jimi.uw_server.model.Task;
import com.jimi.uw_server.model.Window;

/**
 * LS、SL命令处理器
 * <br>
 * <b>2018年7月10日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class LSSLHandler {

	private static final String ENABLED_ROBOT_SQL = "SELECT * FROM robot WHERE enabled = ?";
	private static final String SPECIFIED_ID_MATERIAL_TYPE_SQL = "SELECT * FROM material_type WHERE id IN()";
	private static final String SPECIFIED_POSITION_MATERIAL_TYEP_SQL = "SELECT * FROM material_type WHERE row = ? AND col = ? AND height = ?";
	
	
	/**
	 * 发送LS指令
	 */
	public static void sendLS() {
		//判断是否存在停止分配标志位
		if(TaskItemRedisDAO.isPauseAssign() == 1){
			return;
		}

		//判断til是否为空
		List<AGVIOTaskItem> taskItems = new ArrayList<>();
		TaskItemRedisDAO.appendTaskItems(taskItems);
		if (taskItems.isEmpty()) {
			TaskItemRedisDAO.setLcn(0);
			return;
		}
		
		//统计当前有效robot数目赋值到cn
		int cn = Robot.dao.find(ENABLED_ROBOT_SQL, 1).size();
		int lcn = Integer.valueOf(TaskItemRedisDAO.getLcn());
		if (lcn > cn - 1) {
			lcn = cn - 1;
			TaskItemRedisDAO.setLcn(lcn);
			return;
		}
		int b = cn;
		cn = cn - lcn;
		lcn = b - 1;
		int a = 0;
		TaskItemRedisDAO.setLcn(lcn);
		
		//根据materialType表生成物料是否在架情况映射mtc
		Map<Integer, MaterialType> mtc = new HashMap<>();
		for (AGVIOTaskItem item : taskItems) {
			mtc.put(item.getPackingListItem().getMaterialTypeId(), null);
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
			MaterialType materialType = mtc.get(item.getPackingListItem().getMaterialTypeId());
			
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
				
				//发送LS>>>
				AGVMoveCmd cmd = createLSCmd(materialType, item);
				AGVMainSocket.sendMessage(Json.getJson().toJson(cmd));
				
				//在数据库标记所有处于该坐标的物料为不在架***
				List<MaterialType> specifiedPositionMaterialTypes = MaterialType.dao.find(SPECIFIED_POSITION_MATERIAL_TYEP_SQL,
						materialType.getRow(), materialType.getCol(), materialType.getHeight());
				for (MaterialType mt: specifiedPositionMaterialTypes) {
					mt.setIsOnShelf(false);
					mt.update();
				}
				
				//更新任务条目状态为已分配***
				TaskItemRedisDAO.updateTaskItemState(item, 1);
				
				cn--;
			}
			a++;
		}
	}


	/**
	 * 处理Status指令
	 */
	public static void handleStatus(String message) {
		//转换成实体类
		AGVStatusCmd statusCmd = Json.getJson().parse(message, AGVStatusCmd.class);
		
		//判断是否是第二动作完成
		if(statusCmd.getStatus() != 2) {
			return;
		}
		
		//获取groupid
		String groupid = statusCmd.getMissiongroupid();
		
		//判断是LS指令还是SL指令第二动作完成，状态是1说明是LS，状态2是SL
		for (AGVIOTaskItem item : TaskItemRedisDAO.getTaskItems()) {
			if(groupid.equals(item.getGroupId())) {
				//查询对应物料类型
				MaterialType materialType = MaterialType.dao.findById(groupid.split(":")[0]);
				
				if(item.getState() == 1) {//LS执行完成时：（这部分逻辑下一个版本放到APP中）
					//构建SL指令，令指定robot把料送回原仓位
					AGVMoveCmd moveCmd = createSLCmd(statusCmd, groupid, materialType, item);
					//发送SL>>>
					AGVMainSocket.sendMessage(Json.getJson().toJson(moveCmd));
					
					//更改taskitems里对应item状态为2（已拣料到站）***
					TaskItemRedisDAO.updateTaskItemState(item, 2);
				}else if(item.getState() == 2) {//SL执行完成时：
					//在数据库标记所有处于该坐标的物料为在架***
					List<MaterialType> specifiedPositionMaterialTypes = MaterialType.dao.find(SPECIFIED_POSITION_MATERIAL_TYEP_SQL,
							materialType.getRow(), materialType.getCol(), materialType.getHeight());
					for (MaterialType mt: specifiedPositionMaterialTypes) {
						mt.setIsOnShelf(true);
						mt.update();
					}
					
					//更改taskitems里对应item状态为3（已回库完成）***
					TaskItemRedisDAO.updateTaskItemState(item, 3);
					
					/*
					 * 判断该groupid所在的任务是否全部条目状态为3（已回库完成），如果是，
					 * 则清除所有该任务id对应的条目，释放内存，并修改数据库任务状态***
					*/
					boolean isAllFinish = true;
					for (AGVIOTaskItem item1 : TaskItemRedisDAO.getTaskItems()) {
						if(groupid.split(":")[1].equals(item1.getGroupId().split(":")[1]) && item1.getState() != 3) {
							isAllFinish = false;
						}
					}
					if(isAllFinish) {
						int taskId = Integer.valueOf(groupid.split(":")[3]);
						TaskItemRedisDAO.removeTaskItemByTaskId(taskId);
						Task task = new Task();
						task.setId(taskId);
						task.setState(3);
						task.update();
					}
					
					//调用指令发送发方法发送下一条指令
					sendLS();
				}
			}
		}
	}


	private static AGVMoveCmd createSLCmd(AGVStatusCmd statusCmd, String groupid, MaterialType materialType,
			AGVIOTaskItem item) {
		List<AGVMissionGroup> groups = new ArrayList<>();
		AGVMissionGroup group = new AGVMissionGroup();
		group.setMissiongroupid(groupid);//missionGroupId要和LS指令相同
		group.setRobotid(statusCmd.getRobotid());//robotId要和LS指令相同
		int windowId = Task.dao.findById(item.getPackingListItem().getTaskId()).getWindow();
		Window window = Window.dao.findById(windowId);
		group.setStartx(window.getRow());//起点X为仓口X
		group.setStarty(window.getCol());//起点Y为仓口Y
		group.setEndx(materialType.getRow());//设置X
		group.setEndy(materialType.getCol());//设置Y
		group.setEndz(materialType.getHeight());//设置Z
		groups.add(group);
		AGVMoveCmd moveCmd = new AGVMoveCmd();
		moveCmd.setCmdcode("SL");
		moveCmd.setCmdid(TaskItemRedisDAO.getCmdId());
		moveCmd.setMissiongroups(groups);
		return moveCmd;
	}


	private static AGVMoveCmd createLSCmd(MaterialType materialType, AGVIOTaskItem item) {
		AGVMissionGroup group = new AGVMissionGroup();
		group.setMissiongroupid(item.getGroupId());
		group.setRobotid(0);//让AGV系统自动分配
		group.setStartx(materialType.getRow());//物料Row
		group.setStarty(materialType.getCol());//物料Col
		group.setStartz(materialType.getHeight());//物料Height
		int windowId = Task.dao.findById(item.getPackingListItem().getTaskId()).getWindow();
		Window window = Window.dao.findById(windowId);
		group.setEndx(window.getRow());//终点X为仓口X
		group.setEndy(window.getCol());//终点Y为仓口Y
		List<AGVMissionGroup> groups = new ArrayList<>();
		groups.add(group);
		AGVMoveCmd cmd = new AGVMoveCmd();
		cmd.setCmdcode("LS");
		cmd.setCmdid(TaskItemRedisDAO.getCmdId());
		cmd.setMissiongroups(groups);
		return cmd;
	}
	
}
