package com.jimi.uw_server.agv.entity.bo;

import com.jimi.uw_server.model.PackingListItem;

/**
 * AGV出入库任务条目 <br>
 * <b>2018年6月15日</b>
 * 
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AGVIOTaskItem {

	private Integer id;

	private Integer taskId;

	private Integer materialTypeId;

	private Integer quantity;

	private Integer robotId;

	/**
	 * 0：未分配 1：已分配拣料 2：已拣料到站 3：已分配回库 4：已回库完成
	 */
	private Integer state;

	
	public AGVIOTaskItem() {}
	
	
	public AGVIOTaskItem(PackingListItem packingListItem) {
		this.id = packingListItem.getId();
		this.taskId = packingListItem.getTaskId();
		this.materialTypeId = packingListItem.getMaterialTypeId();
		this.quantity = packingListItem.getQuantity();
		this.robotId = 0;
		this.state = 0;
	}

	public Integer getRobotId() {
		return robotId;
	}

	public void setRobotId(Integer robotId) {
		this.robotId = robotId;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Integer getMaterialTypeId() {
		return materialTypeId;
	}

	public void setMaterialTypeId(Integer materialTypeId) {
		this.materialTypeId = materialTypeId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getGroupId() {
		return materialTypeId + ":" + taskId;
	}
}
