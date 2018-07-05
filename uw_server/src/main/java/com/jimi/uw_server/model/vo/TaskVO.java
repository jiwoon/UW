package com.jimi.uw_server.model.vo;

import java.util.Date;

/**
 * @author HardyYao
 * @createTime 2018年7月5日 上午11:21:03 
 */
public class TaskVO {
	
	private Integer id;
	
	private Integer state;
	
	private Integer type;
	
	private String fileName;
	
	private Date createTime;

	private String typeString;
	
	private String stateString;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	public void setStateString(String stateString) {
		this.stateString = stateString;
	}

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
		}  else if (this.getState() == 2) {
			this.stateString = "已完成";
		} else if (this.getState() == 4) {
			this.stateString = "已作废";
		}
		return stateString;
	}
	
	public TaskVO(Integer id, Integer state, Integer type, String fileName, Date createTime) {
		this.id = id;
		this.state = state;
		this.type = type;
		this.fileName = fileName;
		this.createTime = createTime;
	}

}
 