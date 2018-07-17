package com.jimi.uw_server.agv.thread;

import com.jimi.uw_server.agv.dao.TaskItemRedisDAO;
import com.jimi.uw_server.agv.handle.LSSLHandler;

/**
 * 任务池，负责分配任务
 * <br>
 * <b>2018年7月13日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class TaskPool extends Thread{

	private static final long TASK_POOL_CYCLE= 3000;
	
	@Override
	public void run() {
		System.out.println("TaskPool is running NOW...");
		try {
			while(true) {
				sleep(TASK_POOL_CYCLE);
				//判断是否存在停止分配标志位
				if(TaskItemRedisDAO.isPauseAssign() == 1){
					continue;
				}
				LSSLHandler.sendLS();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
