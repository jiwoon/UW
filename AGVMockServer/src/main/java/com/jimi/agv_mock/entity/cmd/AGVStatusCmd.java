package com.jimi.agv_mock.entity.cmd;

import com.jimi.agv_mock.entity.cmd.base.AGVBaseCmd;

/**
 * AGV指令任务状态指令
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AGVStatusCmd extends AGVBaseCmd{

	private String missiongroupid;
	
	private Integer status;
	
	private Integer robotid;

	public String getMissiongroupid() {
		return missiongroupid;
	}

	public void setMissiongroupid(String missiongroupid) {
		this.missiongroupid = missiongroupid;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getRobotid() {
		return robotid;
	}

	public void setRobotid(Integer robotid) {
		this.robotid = robotid;
	}
	
}
