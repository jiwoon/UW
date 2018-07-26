package com.jimi.agv_mock.constant;

/**
 * 常量
 * <br>
 * <b>2018年7月4日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class Constant {
	
	//系统相关
	/**
	 * 叉车数组
	 */
	public static final int[] ROBOT_IDS = new int[] {2222, 3333, 4444, 1111};
	/**
	 * 叉车实时信息发送周期
	 */
	public static final int ROBOT_INFO_CYCLE = 1000;
	/**
	 * 任务池遍历周期
	 */
	public static final int TASK_POOL_CYCLE = 500;
	/**
	 * 等待ACK最大等待时间
	 */
	public static final int WAIT_ACK_TIMEOUT = 3000;
	
	
	
	//任务相关
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
	public static final int TASK_FLOATING_PERCENTAGE = 20;
	
	
	
	//干扰器相关
	/**
	 * 干扰总开关（影响异常的产生、回复ACK的是否丢失）
	 */
	public static final boolean DISTURB_SWITCH = false;
	/**
	 * 任务执行时每秒产生普通异常的概率
	 */
	public static final int EXCEPTION_PERCENTAGE = 2;
	/**
	 * 第一动作完成后产生负载异常的概率
	 */
	public static final int LOAD_EXCEPTION_PERCENTAGE = 15;
	/**
	 * 不回复ACK的概率
	 */
	public static final int DONT_SEND_ACK_PERCENTAGE = 33;
	/**
	 * 回复ACK的基础延迟
	 */
	public static final int SEND_ACK_DELAY = 3000;
	/**
	 * 回复ACK的延迟的随机浮动百分比
	 */
	public static final int ACK_FLOATING_PERCENTAGE = 20;
	
}
