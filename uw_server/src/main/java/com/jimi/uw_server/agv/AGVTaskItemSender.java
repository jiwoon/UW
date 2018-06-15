package com.jimi.uw_server.agv;

import java.util.List;

import com.jimi.uw_server.agv.entity.AGVTaskItem;

/**
 * AGV任务条目发送者
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AGVTaskItemSender {

	/**
	 * 指令发送方法<br>该方法由AGVWebSocket进行调用，请勿在其他地方调用
	 */
	public synchronized static void send() {
		
	}
	
	
	/**
	 * 添加任务条目，该方法会把新的任务条目均匀插入到现有的任务列表当中<br>
	 * 该方法由任务业务层的开始任务方法调用，请勿在其他地方调用
	 */
	public synchronized static void addTaskItem(List<AGVTaskItem> taskItems) {
		
	}
	
	
	/**
	 * 删除指定任务id的条目<br>
	 * 该方法由任务业务层的作废任务方法调用，请勿在其他地方调用
	 */
	public synchronized static void removeTaskItem(int taskId) {
		
	}
}
