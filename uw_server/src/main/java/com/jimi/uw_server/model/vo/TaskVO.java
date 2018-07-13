package com.jimi.uw_server.model.vo;

import java.util.Date;

import com.jimi.uw_server.model.Task;

public class TaskVO extends Task{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5623582632249846864L;

	private String typeString;
	
	private String stateString;
	
	public String getTypeString(Integer type) {
		if (type == 0) {
			this.typeString = "入库";
		} else if (type == 1) {
			this.typeString = "出库";
		} else if (type == 2) {
			this.typeString = "盘点";
		}  else if (type == 3) {
			this.typeString = "位置优化";
		}
		return typeString;
	}

	public String getStateString(Integer state) {
		if (state == 0) {
			this.stateString = "未审核";
		} else if (state == 1) {
			this.stateString = "未开始";
		} else if (state == 2) {
			this.stateString = "进行中";
		}  else if (state == 3) {
			this.stateString = "已完成";
		} else if (state == 4) {
			this.stateString = "已作废";
		}
		return stateString;
	}


	public TaskVO(Integer id, Integer state, Integer type, String fileName, Date createTime) {
		this.setId(id);
		this.setState(state);
		this.set("stateString", getStateString(state));
		this.setType(type);
		this.set("typeString", getTypeString(type));
		this.set("fileName", fileName);
		this.set("createTimeString", createTime);
	}

	
}
