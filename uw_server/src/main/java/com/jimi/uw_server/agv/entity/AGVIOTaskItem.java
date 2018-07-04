package com.jimi.uw_server.agv.entity;

/**
 * AGV出入库任务条目
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AGVIOTaskItem {

	private int materialTypeId;
	
	private int windowPositionX;
	
	private int windowPositionY;
	
	private int taskId;
	
	/**
	 * 0：未分配
	 * 1：已分配
	 */
	private int state;

	
	public int getMaterialTypeId() {
		return materialTypeId;
	}

	public void setMaterialTypeId(int materialId) {
		this.materialTypeId = materialId;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getWindowPositionY() {
		return windowPositionY;
	}

	public void setWindowPositionY(int windowPositionY) {
		this.windowPositionY = windowPositionY;
	}

	public int getWindowPositionX() {
		return windowPositionX;
	}

	public void setWindowPositionX(int windowPositionX) {
		this.windowPositionX = windowPositionX;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public AGVIOTaskItem(int materialId, int windowPositionX, int windowPositionY, int taskId) {
		this.materialTypeId = materialId;
		this.windowPositionX = windowPositionX;
		this.windowPositionY = windowPositionY;
		this.taskId = taskId;
		this.state = 0;
	}
	
	
	/**
	 * 把GroupId转成Item
	 */
	@Override
	public String toString() {
		return materialTypeId + ":" + windowPositionX + ":" + windowPositionY + ":" + taskId + "#" + state;
	}
	
	
	/**
	 * 把Item转成GroupId
	 */
	public static AGVIOTaskItem fromString(String string) {
		String[] attrsAndState = string.split("#");
		String[] attrs = attrsAndState[0].split(":");
		AGVIOTaskItem item = new AGVIOTaskItem(Integer.valueOf(attrs[0]), Integer.valueOf(attrs[1]), Integer.valueOf(attrs[2]), Integer.valueOf(attrs[3]));
		item.setState(Integer.valueOf(attrsAndState[1]));
		return item;
	}
}
