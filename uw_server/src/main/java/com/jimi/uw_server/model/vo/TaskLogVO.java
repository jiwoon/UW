package com.jimi.uw_server.model.vo;

import java.util.Date;

import com.jimi.uw_server.model.Material;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.model.TaskLog;
import com.jimi.uw_server.model.User;

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
	
	private String operatorName;
	
	public String getAutoString(boolean auto) {
		if(auto) {
			this.autoString = "自动";
		} else {
			this.autoString = "手动";
		}
		return autoString;
	}

	public String getTaskType(Integer taskId) {
		if (taskId == 0) {
			this.taskType = "入库";
		} else if (taskId == 1) {
			this.taskType = "出库";
		} else if (taskId == 2) {
			this.taskType = "盘点";
		}  else if (taskId == 3) {
			this.taskType = "位置优化";
		}
		return taskType;
	}

	public String getOperatorName(String operator) {
		User user = User.dao.findById(operator);
		operatorName = user.getName();
		return operatorName;
	}

	public String getMaterialNo(String materialId) {
		Material material = Material.dao.findById(materialId);
		Integer type = material.getType();
		MaterialType materialType = MaterialType.dao.findById(type);
		materialNo = materialType.getNo();
		return materialNo;
	}

	public TaskLogVO(Integer id, Integer taskId, String taskType, String materialId, Integer quantity,
			String operator, boolean auto, Date time) {
		this.setId(id);
		this.set("taskId", taskId);
		this.set("taskType", getTaskType(taskId));
		this.set("materialId", materialId);
		this.set("materialNo", getMaterialNo(materialId));
		this.setQuantity(quantity);
		this.setOperator(operator);
		this.set("operatorName", getOperatorName(operator));
		this.set("auto", getAutoString(auto));
		this.setTime(time);
	}


}
 