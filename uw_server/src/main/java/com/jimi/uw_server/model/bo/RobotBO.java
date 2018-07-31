package com.jimi.uw_server.model.bo;

public class RobotBO {

	private Integer id;
	
	private Integer status;
	
	private Integer battery;
	
	private Integer x;
	
	private Integer y;
	
	private Integer enabled;
	
	private Integer error;
	
	private Integer warn;
	
	private Boolean pause;
	
	private Boolean loadException;
	
	public RobotBO() {
		id = -1;
		status = -1;
		battery = -1;
		x = -1;
		y = -1;
		enabled = -1;
		error = -1;
		warn = -1;
		pause = false;
		loadException = false;
	}

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

	public Integer getError() {
		return error;
	}

	public void setError(Integer error) {
		this.error = error;
	}

	public Integer getWarn() {
		return warn;
	}

	public void setWarn(Integer warn) {
		this.warn = warn;
	}

	public Boolean getPause() {
		return pause;
	}

	public void setPause(Boolean pause) {
		this.pause = pause;
	}

	public Boolean getLoadException() {
		return loadException;
	}

	public void setLoadException(Boolean loadException) {
		this.loadException = loadException;
	}
	
}
