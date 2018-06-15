package com.jimi.uw_server.agv.entity;

/**
 * AGV任务条目
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AGVTaskItem {

	private int materialId;
	
	private int windowPositionX;
	
	private int windowPositionY;
	
	private int taskId;

	
	public int getMaterialId() {
		return materialId;
	}

	public void setMaterialId(int materialId) {
		this.materialId = materialId;
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

	public AGVTaskItem(int materialId, int windowPositionX, int windowPositionY, int taskId) {
		this.materialId = materialId;
		this.windowPositionX = windowPositionX;
		this.windowPositionY = windowPositionY;
		this.taskId = taskId;
	}
	
}
