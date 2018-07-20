package com.jimi.uw_server.agv.entity.bo;

import com.jimi.uw_server.model.Robot;

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
	
	public static Robot toModel(AGVRobot agvRobot) {
		Robot robot = new Robot();
		robot.setBattery(agvRobot.getBatteryPower());
		robot.setEnabled(agvRobot.getEnable());
		robot.setError(agvRobot.getErrorcode());
		robot.setWarn(agvRobot.getWarncode());
		robot.setX(agvRobot.getPosX());
		robot.setY(agvRobot.getPosY());
		robot.setStatus(agvRobot.getStatus());
		robot.setPause(agvRobot.getSystem_pause());
		robot.setId(agvRobot.getRobotid());
		return robot;
	}
	
	
	public static AGVRobot fromModel(Robot robot) {
		AGVRobot agvRobot = new AGVRobot();
		agvRobot.setBatteryPower(robot.getBattery());
		agvRobot.setEnable(robot.getEnabled());
		agvRobot.setErrorcode(robot.getError());
		agvRobot.setWarncode(robot.getWarn());
		agvRobot.setPosX(robot.getX());
		agvRobot.setPosY(robot.getY());
		agvRobot.setStatus(robot.getStatus());
		agvRobot.setSystem_pause(robot.getPause());
		agvRobot.setRobotid(robot.getId());
		return agvRobot;
	}
}
