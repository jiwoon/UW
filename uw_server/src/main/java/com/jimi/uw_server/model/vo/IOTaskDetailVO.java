package com.jimi.uw_server.model.vo;

import java.util.Date;

import com.jimi.uw_server.model.TaskLog;

/** 
 * 
 * @author HardyYao
 * @createTime 2018年7月13日 上午8:29:22
 */
public class IOTaskDetailVO extends TaskLog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3361161172340670552L;

	public IOTaskDetailVO(String materialNo, Integer requestQuantity, Integer actualQuantity, Date finishTime) {
		this.set("materialNo", materialNo);
		this.set("requestQuantity", requestQuantity);
		this.set("actualQuantity", actualQuantity);
		this.set("finishTime", finishTime);
	}

}
