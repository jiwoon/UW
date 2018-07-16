package com.jimi.agv_mock.constant;

/**
 * 常量
 * <br>
 * <b>2018年7月4日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class Constant {
	
	/**
	 * ACK最大等待时间
	 */
	public static final int WAIT_ACK_TIMEOUT = 3000;
	
	/**
	 * 从收到任务到开始执行的间隔时间
	 */
	public static final int START_CMD_DELAY = 5000;
	
	/**
	 * 从开始执行到第一动作完成的间隔时间
	 */
	public static final int FIRST_ACTION_DELAY = 9000;
	
	/**
	 * 第一动作完成到第二动作完成的间隔时间
	 */
	public static final int SECOND_ACTION_DELAY = 9000;
	
	/**
	 * 间隔时间的随机浮动百分比
	 */
	public static final int FLOATING_PERCENTAGE = 20;
	
}
