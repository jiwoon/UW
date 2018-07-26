package com.jimi.uw_server.model.vo;

import java.util.Date;
import java.util.List;

import com.jimi.uw_server.model.TaskLog;

/**
 * 
 * @author HardyYao
 * @createTime 2018年7月23日 下午4:04:25
 */
public class WindowTaskItemsVO extends TaskLog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2631121149118866618L;
	
	private List<?> details;

	public Integer getActualQuantity(Integer MaterialTypeId) {
		
		return MaterialTypeId;
	}

	public WindowTaskItemsVO(Integer packingListItemId, String fileName, String materialNo, Integer planQuantity, Integer actualQuantity, Date finishTime) {
		this.set("id", packingListItemId);
		this.set("fileName", fileName);
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
