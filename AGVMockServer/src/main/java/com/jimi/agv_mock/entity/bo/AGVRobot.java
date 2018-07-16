package com.jimi.agv_mock.entity.bo;

/**
 * 机器信息组
 * <br>
 * <b>2018年7月13日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AGVRobot{

	private Integer robotid;
	
	private Integer status;
	
	private Integer batteryPower;
	
	private Integer posX;
	
	private Integer posY;
	
	private Integer errorcode;
	
	private Integer enable;
	
	private Integer warncode;
	
	private Boolean system_pause;

	public Integer getRobotid() {
		return robotid;
	}

	public void setRobotid(Integer robotid) {
		this.robotid = robotid;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getBatteryPower() {
		return batteryPower;
	}

	public void setBatteryPower(Integer batteryPower) {
		this.batteryPower = batteryPower;
	}

	public Integer getPosX() {
		return posX;
	}

	public void setPosX(Integer posX) {
		this.posX = posX;
	}

	public Integer getPosY() {
		return posY;
	}

	public void setPosY(Integer posY) {
		this.posY = posY;
	}

	public Integer getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(Integer errorcode) {
		this.errorcode = errorcode;
	}

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public Integer getWarncode() {
		return warncode;
	}

	public void setWarncode(Integer warncode) {
		this.warncode = warncode;
	}

	public Boolean getSystem_pause() {
		return system_pause;
	}

	public void setSystem_pause(Boolean system_pause) {
		this.system_pause = system_pause;
	}
	
}
