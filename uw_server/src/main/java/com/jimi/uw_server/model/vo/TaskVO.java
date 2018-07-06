package com.jimi.uw_server.model.vo;

import java.util.Date;

import com.jimi.uw_server.model.Task;
import com.jimi.uw_server.model.base.BaseTask;

public class TaskVO extends BaseTask<Task> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5623582632249846864L;

	private String typeString;
	
	private String stateString;
	
	public String getTypeString() {
		if (this.getType() == 0) {
			this.typeString = "入库";
		} else if (this.getType() == 1) {
			this.typeString = "出库";
		} else if (this.getType() == 2) {
			this.typeString = "盘点";
		}  else if (this.getType() == 3) {
			this.typeString = "位置优化";
		}
		return typeString;
	}

	public String getStateString() {
		if (this.getState() == 0) {
			this.stateString = "未审核";
		} else if (this.getState() == 1) {
			this.stateString = "未开始";
		} else if (this.getState() == 2) {
			this.stateString = "进行中";
		}  else if (this.getState() == 3) {
			this.stateString = "已完成";
		} else if (this.getState() == 4) {
			this.stateString = "已作废";
		}
		return stateString;
	}


	public TaskVO(Integer id, Integer state, Integer type, String fileName, Date createTime) {
		this.setId(id);
		this.setState(state);
		this.set("stateString", getStateString());
		this.setType(type);
		this.set("typeString", getTypeString());
		this.set("fileName", fileName);
		this.set("createTimeString", createTime);
	}

	
}
