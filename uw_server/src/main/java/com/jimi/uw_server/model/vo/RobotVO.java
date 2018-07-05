package com.jimi.uw_server.model.vo;

/**
 * @author HardyYao
 * @createTime 2018年7月5日 上午11:24:32 
 */
public class RobotVO {
	
	private Integer id;
	
	private Integer status;
	
	private Integer battery;
	
	private Integer x;
	
	private Integer y;
	
	private Integer enabled;
	
	private String enableString;
	
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getBattery() {
		return battery;
	}

	public void setBattery(Integer battery) {
		this.battery = battery;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public void setEnableString(String enableString) {
		this.enableString = enableString;
	}

	public String getEnableString() {
		if(this.enabled == 1) {
			this.enableString = "是";
		} else {
			this.enableString = "否";
		}
		return enableString;
	}
	
	public RobotVO(Integer id, Integer status, Integer battery, Integer x, Integer y, Integer enabled) {
		this.id = id;
		this.status = status;
		this.battery = battery;
		this.x = x;
		this.y = y;
		this.enabled = enabled;
	}

}
 