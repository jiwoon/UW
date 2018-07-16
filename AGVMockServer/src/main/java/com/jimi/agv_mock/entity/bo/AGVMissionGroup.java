package com.jimi.agv_mock.entity.bo;

/**
 * AGV任务组
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AGVMissionGroup {

	private Integer startx;
	
	private Integer starty;
	
	private Integer startz;
	
	private Integer endx;
	
	private Integer endy;
	
	private Integer endz;
	
	private Integer robotid;
	
	private String missiongroupid;

	public Integer getStartx() {
		return startx;
	}

	public void setStartx(Integer startx) {
		this.startx = startx;
	}

	public Integer getStarty() {
		return starty;
	}

	public void setStarty(Integer starty) {
		this.starty = starty;
	}

	public Integer getStartz() {
		return startz;
	}

	public void setStartz(Integer startz) {
		this.startz = startz;
	}

	public Integer getEndx() {
		return endx;
	}

	public void setEndx(Integer endx) {
		this.endx = endx;
	}

	public Integer getEndy() {
		return endy;
	}

	public void setEndy(Integer endy) {
		this.endy = endy;
	}

	public Integer getEndz() {
		return endz;
	}

	public void setEndz(Integer endz) {
		this.endz = endz;
	}

	public Integer getRobotid() {
		return robotid;
	}

	public void setRobotid(Integer robotid) {
		this.robotid = robotid;
	}

	public String getMissiongroupid() {
		return missiongroupid;
	}

	public void setMissiongroupid(String missiongroupid) {
		this.missiongroupid = missiongroupid;
	}
	
	
}
