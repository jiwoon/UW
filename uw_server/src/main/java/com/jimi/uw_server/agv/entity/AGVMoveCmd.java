package com.jimi.uw_server.agv.entity;

import java.util.List;

/**
 * AGV指令任务状态指令
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AGVMoveCmd extends AGVBaseCmd{

	private List<AGVMissionGroup> missiongroups;

	public List<AGVMissionGroup> getMissiongroups() {
		return missiongroups;
	}

	public void setMissiongroups(List<AGVMissionGroup> missiongroups) {
		this.missiongroups = missiongroups;
	}
	
}
