package com.jimi.uw_server.model.vo;

/**
 * @author HardyYao
 * @createTime 2018年7月5日 上午11:19:22 
 */
public class UserVO {

	private String uid;
	
	private String password;
	
	private String name;
	
	private Integer type;
	
	private boolean enabled;
	
	private String enabledString;
	
	private String typeString;
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setEnabledString(String enabledString) {
		this.enabledString = enabledString;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	public String getEnabledString() {
		if (this.isEnabled()) {
			this.enabledString = "是";
		} else {
			this.enabledString = "否";
		}
		return enabledString;
	}

	public String getTypeString() {
		if (this.getType() == 1) {
			this.typeString = "超级管理员";
		} else {
			this.typeString = "普通管理员";
		}
		return typeString;
	}
	
	public UserVO(String uid, String password, String name, Integer type, boolean enabled) {
		this.uid = uid;
		this.password = password;
		this.name = name;
		this.type = type;
		this.enabled = enabled;
	}

}
 