package com.jimi.uw_server.model.vo;

import com.jimi.uw_server.model.Robot;

public class RobotVO extends Robot {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7762806826429433369L;
	
	private String enableString;

	public String getEnableString() {
		if(this.getEnabled() == 1) {
			this.enableString = "是";
		} else {
			this.enableString = "否";
		}
		return enableString;
	}
	
	public RobotVO(Integer id, Integer status, Integer battery, int x, int y, Integer enabled) {
		this.setId(id);
		this.setStatus(status);
		this.setBattery(battery);
		this.setX(x);
		this.setY(y);
		this.setEnabled(enabled);
	}
	
}
