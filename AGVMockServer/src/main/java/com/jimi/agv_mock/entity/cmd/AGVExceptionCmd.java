package com.jimi.agv_mock.entity.cmd;

import com.jimi.agv_mock.entity.cmd.base.AGVBaseCmd;

/**
 * 负债异常状态指令
 * <br>
 * <b>2018年7月13日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AGVExceptionCmd extends AGVBaseCmd{

	private String missiongroupid;
	
	private Integer robotid;
	
	private Integer errorcode;

	public String getMissiongroupid() {
		return missiongroupid;
	}

	public void setMissiongroupid(String missiongroupid) {
		this.missiongroupid = missiongroupid;
	}

	public Integer getRobotid() {
		return robotid;
	}

	public void setRobotid(Integer robotid) {
		this.robotid = robotid;
	}

	public Integer getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(Integer errorcode) {
		this.errorcode = errorcode;
	}
	
}
