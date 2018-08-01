package com.jimi.agv_mock.map;

/**
 * 坐标类，组成仓库地图的单元
 * <br>
 * <b>2018年7月31日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class Position {

	private int x;
	
	private int y;
	
	private boolean parking;
	
	
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
		this.parking = false;
	}


	public int getX() {
		return x;
	}


	public void setX(int x) {
		this.x = x;
	}


	public int getY() {
		return y;
	}


	public void setY(int y) {
		this.y = y;
	}


	public boolean isParking() {
		return parking;
	}


	public void setParking(boolean parking) {
		this.parking = parking;
	}
	
}
