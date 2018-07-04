package com.jimi.agv_mock.entity;

/**
 * AGV取消任务指令类
 * <br>
 * <b>2018年6月28日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AGVCancelCmd extends AGVBaseCmd{

	private String missiongroupid;

	public String getMissiongroupid() {
		return missiongroupid;
	}

	public void setMissiongroupid(String missiongroupid) {
		this.missiongroupid = missiongroupid;
	}
	
}
