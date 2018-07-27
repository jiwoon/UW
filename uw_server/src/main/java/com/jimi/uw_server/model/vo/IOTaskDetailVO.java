package com.jimi.uw_server.model.vo;

import java.util.Date;
import java.util.List;

import com.jimi.uw_server.model.TaskLog;

/** 
 * 
 * @author HardyYao
 * @createTime 2018年7月13日 上午8:29:22
 */
public class IOTaskDetailVO extends TaskLog {
	
	private static final long serialVersionUID = -3361161172340670552L;

	private List<?> details;

	public Integer getActualQuantity(Integer MaterialTypeId) {
		
		return MaterialTypeId;
	}

	public IOTaskDetailVO(Integer packingListItemId, String materialNo, Integer planQuantity, Integer actualQuantity, Date finishTime) {
		this.set("id", packingListItemId);
		this.set("materialNo", materialNo);
		this.set("planQuantity", planQuantity);
		this.set("actualQuantity", actualQuantity);
		if (finishTime == null) {
			this.set("finishTime", "no");
		} else {
			this.set("finishTime", finishTime);
		}
	}

	public List<?> getDetails() {
		return details;
	}

	public void setDetails(List<?> details) {
		this.set("details", details);
	}


}
