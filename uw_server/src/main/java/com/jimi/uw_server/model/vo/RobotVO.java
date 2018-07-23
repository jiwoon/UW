package com.jimi.uw_server.model.vo;

import com.jimi.uw_server.model.Robot;

/**
 * @author HardyYao
 * @createTime 2018年7月5日 上午11:24:32 
 */
public class RobotVO extends Robot{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7771022031487042995L;

	private String enabledString;

	private String errorString;

	private String warnString;

	private String pauseString;
	
	private String loadExceptionString;

	public void setEnabledString(String enabledString) {
		this.enabledString = enabledString;
	}

	public String getEnabledString(Integer enabled) {
		switch(enabled) {
		case 0:
			enabledString = "已禁用";
			break;
		case 1:
			enabledString = "已禁用";
			break;
		case 2:
			this.enabledString = "已启用";
			break;
		}
		return enabledString;
	}

	public String getErrorString(Integer error) {
		switch(error) {
		case 0:
			errorString = "直行出线";
			break;
		case 1:
			errorString = "停车过线";
			break;
		case 2:
			errorString = "气压碰撞";
			break;
		case 3:
			errorString = "叉杆撞托板";
			break;
		case 4:
			errorString = "叉杆撞障碍物";
			break;
		case 5:
			errorString = "小车顶货架";
			break;
		case 6:
			errorString = "红外避障";
			break;
		case 7:
			errorString = "wifi通讯异常";
			break;
		case 8:
			errorString = "SPI校验错误";
			break;
		case 9:
			errorString = "SPI通讯超时";
			break;
		case 10:
			errorString = "角度传感器超时";
			break;
		case 11:
			errorString = "前摄像头超时";
			break;
		case 12:
			errorString = "中摄像头超时";
			break;
		case 13:
			errorString = "后摄像头超时";
			break;
		case 14:
			errorString = "升降杆堵转";
			break;
		case 15:
			errorString = "升降杆超界";
			break;
		case 16:
			errorString = "控制器超时";
			break;
		case 17:
			errorString = "电池电压低";
			break;
		case 18:
			errorString = "充电器连接失败";
			break;
		case 19:
			errorString = "电池充电电压异常";
			break;
		case 20:
			errorString = "轮毂堵转";
			break;
		case 21:
			errorString = "轮毂速度异常";
			break;
		case 22:
			errorString = "升降杆保险管异常";
			break;
		case 23:
			errorString = "电源开关断开";
			break;
		case 24:
			errorString = "紧急开关闭合";
			break;
		case 25:
			errorString = "命令错误";
			break;
		case 255:
			errorString = "";
			break;
		}
		return errorString;
	}

	public String getWarnString(Integer warn) {
		switch(warn) {
		case 0:
			warnString = "前摄像头异常";
			break;
		case 1:
			warnString = "中摄像头异常";
			break;
		case 2:
			warnString = "后摄像头异常";
			break;
		case 4:
			warnString = "电量低";
			break;
		case 5:
			warnString = "wifi 故障";
			break;
		case 6:
			warnString = "该机器已经断线超过三分钟以上";
			break;
		case 255:
			warnString = "";
			break;
		}
	
		return warnString;
	}

	public String getPauseString(boolean pause) {
		if (pause) {
			pauseString = "已暂停";
 		} else {
 			pauseString = "已启动";
 		}
		return pauseString;
	}
	
	public String getLoadExceptionString(boolean loadException) {
		if (loadException) {
			loadExceptionString = "取空异常";
		} else {
			loadExceptionString = "";
		}
		return loadExceptionString;
	}

	public RobotVO(Integer id, Integer status, Integer battery, int x, int y, Integer enabled, Integer error, Integer warn, boolean pause, 
			boolean loadException) {
		this.setId(id);
		this.setStatus(status);
		this.setBattery(battery);
		this.setX(x);
		this.setY(y);
		this.setEnabled(enabled);
		this.set("enabledString", getEnabledString(enabled));
		this.set("error", error);
		this.set("errorString", getErrorString(error));
		this.set("warn", warn);
		this.set("warnString", getWarnString(warn));
		this.setPause(pause);
		this.set("pauseString", getPauseString(pause));
		this.set("loadException", loadException);
		this.set("loadExceptionString", getLoadExceptionString(loadException));
	}

}
 