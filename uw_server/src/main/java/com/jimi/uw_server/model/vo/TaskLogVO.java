package com.jimi.uw_server.model.vo;

import java.util.Date;

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
	
	private String autoString;

	private String taskType;
	
	private String materialNo;
	
	public String getAutoString() {
		if(this.getAuto()) {
			this.autoString = "自动";
		} else {
			this.autoString = "手动";
		}
		return autoString;
	}

	public void setAuto(String autoString) {
		this.autoString = autoString;
	}

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
	
	public TaskLogVO(Integer id, Integer taskId, String taskType, String materialId, String materialNo, Integer quantity,
			String operator, boolean auto, Date time) {
		this.setId(id);
		this.setTaskId(taskId);
		this.setTaskType(taskType);;
		this.setMaterialId(materialId);
		this.setMaterialNo(materialNo);
		this.setQuantity(quantity);
		this.setOperator(operator);
		this.setAuto(auto);
		this.setTime(time);
	}


}
 