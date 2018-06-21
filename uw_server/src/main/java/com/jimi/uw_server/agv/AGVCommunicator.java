package com.jimi.uw_server.agv;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.jimi.uw_server.agv.entity.AGVIOTaskItem;
import com.jimi.uw_server.agv.entity.AGVMissionGroup;
import com.jimi.uw_server.agv.entity.AGVMoveCmd;
import com.jimi.uw_server.model.MaterialType;

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
	 * 调用该接口，向AGV系统提交一个出入库任务条目
	 */
	public static void pushIOTaskItem(AGVIOTaskItem item) {
		AGVMoveCmd cmd = createSLCmd(item);
		webSocket.sendMessage(JSON.toJSONString(cmd));
	}
	
	
	private static AGVMoveCmd createSLCmd(AGVIOTaskItem item) {
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
		cmd.setCmdid(AGVWebSocket.getCmdId());
		cmd.setMissiongroups(groups);
		return cmd;
	}
}