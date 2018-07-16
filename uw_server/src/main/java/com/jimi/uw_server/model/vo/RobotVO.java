package com.jimi.uw_server.model.vo;

import com.jimi.uw_server.model.Robot;

/**
 * @author HardyYao
 * @createTime 2018年7月5日 上午11:24:32 
 */
public class RobotVO extends Robot{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7771022031487042995L;

	private String enableString;

	private String errorString;

	private String warnString;

	private String pauseString;

	public void setEnableString(String enableString) {
		this.enableString = enableString;
	}

	public String getEnableString(Integer enabled) {
		if(enabled == 1) {
			this.enableString = "是";
		} else {
			this.enableString = "否";
		}
		return enableString;
	}

	public String getErrorString(Integer error) {
		if (error == 255) {
			errorString = "正常";
		} else {
			errorString = "异常";
		}
		return errorString;
	}

	public String getWarnString(Integer warn) {
		if (warn == 255) {
			warnString = "正常";
		} else {
			warnString = "异常";
		}
		return warnString;
	}

	public String getPauseString(boolean pause) {
		if (pause) {
			pauseString = "是";
 		} else {
 			pauseString = "否";
 		}
		return pauseString;
	}

	public RobotVO(Integer id, Integer status, Integer battery, int x, int y, Integer enabled, Integer error, Integer warn, boolean pause) {
		this.setId(id);
		this.setStatus(status);
		this.setBattery(battery);
		this.setX(x);
		this.setY(y);
		this.setEnabled(enabled);
		this.set("enableString", getEnableString(enabled));
		this.set("error", error);
		this.set("errorString", getErrorString(error));
		this.set("warn", warn);
		this.set("warnString", getWarnString(warn));
		this.setPause(pause);
		this.set("pauseString", getPauseString(pause));
	}

}
 