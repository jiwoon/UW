package com.jimi.uw_server.model.vo;

import com.jimi.uw_server.model.TaskLog;

/**
 * @author HardyYao
 * @createTime 2018年7月5日 上午11:22:18 
 */
public class TaskLogVO extends TaskLog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3791534812200792529L;

	private String taskType;
	
	private String materialNo;

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getMaterialNo() {
		return materialNo;
	}

	public void setMaterialNo(String materialNo) {
		this.materialNo = materialNo;
	}


}
 