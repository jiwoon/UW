package com.jimi.uw_server.model.vo;

import java.util.Date;

import com.jimi.uw_server.model.PositionLog;

/**
 * @author HardyYao
 * @createTime 2018年7月5日 下午10:39:41 
 */
public class PositionLogVO extends PositionLog {

	private static final long serialVersionUID = 2088695242093093771L;

	public PositionLogVO(Integer id, Integer taskId, String materialId, String materialNo, Integer oldArea, Integer oldRow, Integer oldCol, 
			Integer oldHeight, Integer newArea, Integer newRow, Integer newCol, Integer newHeight, Date time) {
		this.setId(id);
		this.set("taskId", taskId);
		this.set("materialId", materialId);
		this.set("materialNo", materialNo);
		this.set("oldArea", oldArea);
		this.set("oldRow", oldRow);
		this.set("oldCol", oldCol);
		this.set("oldHeight", oldHeight);
		this.set("newArea", newArea);
		this.set("newRow", newRow);
		this.set("newCol", newCol);
		this.set("newHeight", newHeight);
		this.setTime(time);
	}
}
 