package com.jimi.uw_server.model.vo;

import java.util.Date;

import com.jimi.uw_server.model.TaskLog;
import com.jimi.uw_server.model.User;

/**
 * @author HardyYao
 * @createTime 2018年7月5日 上午11:22:18 
 */
public class TaskLogVO extends TaskLog {
	
	private static final long serialVersionUID = -3791534812200792529L;
	
	private String autoString;

	private String taskTypeString;
	
	private String operatorName;

	public String getAutoString(boolean auto) {
		if(auto) {
			this.autoString = "自动";
		} else {
			this.autoString = "手动";
		}
		return autoString;
	}

	public String getTaskType(Integer taskType) {
		if (taskType == 0) {
			this.taskTypeString = "入库";
		} else if (taskType == 1) {
			this.taskTypeString = "出库";
		} else if (taskType == 2) {
			this.taskTypeString = "盘点";
		}  else if (taskType == 3) {
			this.taskTypeString = "位置优化";
		}
		return taskTypeString;
	}
	
	public String getOperatorName(String operator) {
		User user = User.dao.findById(operator);
		operatorName = user.getName();
		return operatorName;
	}

	public TaskLogVO(Integer id, Integer taskId, Integer type, String materialId, String materialNo, Integer quantity,
			String operator, boolean auto, Date time) {
		this.setId(id);
		this.set("taskId", taskId);
		this.set("taskType", getTaskType(type));
		this.set("materialId", materialId);
		this.set("materialNo", materialNo);
		this.setQuantity(quantity);
		this.setOperator(operator);
		this.set("operatorName", getOperatorName(operator));
		this.set("auto", getAutoString(auto));
		this.setTime(time);
	}

}
 