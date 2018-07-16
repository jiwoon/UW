package com.jimi.uw_server.agv.entity.bo;

import com.jimi.uw_server.model.PackingListItem;

/**
 * AGV出入库任务条目
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AGVIOTaskItem {

	
	private PackingListItem packingListItem;
	
	private int robotId;
	
	/**
	 * 0：未分配
	 * 1：已分配
	 * 2：已拣料到站
	 * 3：已回库完成
	 */
	private int state;
	
	
	public AGVIOTaskItem(PackingListItem packingListItem) {
		this.packingListItem = packingListItem;
		this.robotId = 0;
		this.state = 0;
	}
	

	public PackingListItem getPackingListItem() {
		return packingListItem;
	}

	public void setPackingListItem(PackingListItem packingListItem) {
		this.packingListItem = packingListItem;
	}

	public int getRobotId() {
		return robotId;
	}

	public void setRobotId(int robotId) {
		this.robotId = robotId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public String getGroupId() {
		return packingListItem.getMaterialTypeId() + ":" + packingListItem.getTaskId();
	}
}
