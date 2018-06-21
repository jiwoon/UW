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

	public AGVIOTaskItem(int materialId, int windowPositionX, int windowPositionY, int taskId) {
		this.materialTypeId = materialId;
		this.windowPositionX = windowPositionX;
		this.windowPositionY = windowPositionY;
		this.taskId = taskId;
	}
	
	
	/**
	 * 把GroupId转成Item
	 */
	@Override
	public String toString() {
		return taskId + ":" + windowPositionX + ":" + windowPositionY + ":" + materialTypeId;
	}
	
	
	/**
	 * 把Item转成GroupId
	 */
	public static AGVIOTaskItem fromString(String string) {
		String[] attrs = string.split(":");
		AGVIOTaskItem item = new AGVIOTaskItem(Integer.valueOf(attrs[1]), Integer.valueOf(attrs[2]), Integer.valueOf(attrs[3]), Integer.valueOf(attrs[0]));
		return item;
	}
}
