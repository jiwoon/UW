package com.jimi.uw_server.model.vo;

import com.jimi.uw_server.model.Robot;

/**
 * @author HardyYao
 * @createTime 2018年7月5日 上午11:24:32 
 */
public class RobotVO extends Robot{
	
	private Integer enabled;
	
	private String enableString;
	

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
	
	
	public RobotVO(Integer id, Integer status, Integer battery, int x, int y, Integer enabled) {
		this.setId(id);
		this.setStatus(status);
		this.setBattery(battery);
		this.setX(x);
		this.setY(y);
		this.setEnabled(enabled);
		this.set("enableString", getEnableString());
	}

}
 