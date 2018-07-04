package com.jimi.uw_server.model.vo;

import com.jimi.uw_server.model.User;

public class UserVO extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1701296666161176827L;
	
	private String enabledString;
	
	private String typeString;

	public String getEnabledString() {
		if (this.getEnabled()) {
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
		this.setUid(uid);
		this.setPassword(password);
		this.setName(name);
		this.setType(type);
		this.setEnabled(enabled);
	}


}
