package com.jimi.uw_server.model.vo;

import com.jimi.uw_server.model.bo.RobotBO;

import cc.darhao.dautils.api.FieldUtil;

/**
 * @author HardyYao
 * @createTime 2018年7月5日 上午11:24:32
 */
public class RobotVO extends RobotBO {

	private String enabledString;

	private String errorString;

	private String warnString;

	private String pauseString;

	private String loadExceptionString;

	public String fillEnabledString(Integer enabled) {
		switch (enabled) {
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

	public String fillErrorString(Integer error) {
		switch (error) {
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

	public String fillWarnString(Integer warn) {
		switch (warn) {
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

	public String fillPauseString(Boolean pause) {
		if (pause) {
			pauseString = "已暂停";
		} else {
			pauseString = "已启动";
		}
		return pauseString;
	}

	public String fillLoadExceptionString(Boolean loadException) {
		if (loadException) {
			loadExceptionString = "取空异常";
		} else {
			loadExceptionString = "";
		}
		return loadExceptionString;
	}

	public String getEnabledString() {
		return enabledString;
	}

	public void setEnabledString(String enabledString) {
		this.enabledString = enabledString;
	}

	public String getErrorString() {
		return errorString;
	}

	public void setErrorString(String errorString) {
		this.errorString = errorString;
	}

	public String getWarnString() {
		return warnString;
	}

	public void setWarnString(String warnString) {
		this.warnString = warnString;
	}

	public String getPauseString() {
		return pauseString;
	}

	public void setPauseString(String pauseString) {
		this.pauseString = pauseString;
	}

	public String getLoadExceptionString() {
		return loadExceptionString;
	}

	public void setLoadExceptionString(String loadExceptionString) {
		this.loadExceptionString = loadExceptionString;
	}

	public RobotVO(RobotBO bo) {
		FieldUtil.copy(bo, this);
		this.enabledString = fillEnabledString(bo.getEnabled());
		this.errorString = fillErrorString(bo.getError());
		this.warnString = fillWarnString(bo.getWarn());
		this.pauseString = fillPauseString(bo.getPause());
		this.loadExceptionString = fillLoadExceptionString(bo.getLoadException());
	}

}
