package com.jimi.uw_server.model.vo;

import java.util.Date;

import com.jimi.uw_server.model.PositionLog;

/**
 * @author HardyYao
 * @createTime 2018年7月5日 下午10:39:41 
 */
public class PositionLogVO extends PositionLog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2088695242093093771L;
	
	private String materialNo;

	public String getMaterialNo() {
		return materialNo;
	}

	public void setMaterialNo(String materialNo) {
		this.materialNo = materialNo;
	}
	
	public PositionLogVO(Integer id, Integer taskId, String materialId, Integer oldArea, Integer oldRow, Integer oldCol, 
			Integer oldHeight, Integer newArea, Integer newRow, Integer newCol, Integer newHeight, Date time) {
		this.setId(id);
		this.setTaskId(taskId);
		this.setMaterialId(materialId);
		this.setOldArea(oldArea);
		this.setOldRow(oldRow);
		this.setOldCol(oldCol);
		this.setOldHeight(oldHeight);
		this.setNewArea(newArea);
		this.setNewRow(newRow);
		this.setNewCol(newCol);
		this.setNewHeight(newHeight);
		this.setTime(time);
	}
}
 