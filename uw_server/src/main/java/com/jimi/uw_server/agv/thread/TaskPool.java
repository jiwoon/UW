package com.jimi.uw_server.agv.thread;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.PropKit;
import com.jimi.uw_server.agv.dao.RobotInfoRedisDAO;
import com.jimi.uw_server.agv.dao.TaskItemRedisDAO;
import com.jimi.uw_server.agv.entity.bo.AGVIOTaskItem;
import com.jimi.uw_server.agv.handle.LSSLHandler;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.model.bo.RobotBO;
import com.jimi.uw_server.util.ErrorLogWritter;

/**
 * 任务池，负责分配任务
 * <br>
 * <b>2018年7月13日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class TaskPool extends Thread{

	
	@Override
	public void run() {
		int taskPoolCycle = PropKit.use("properties.ini").getInt("taskPoolCycle");
		System.out.println("TaskPool is running NOW...");
		while(true) {
			try {
				sleep(taskPoolCycle);
				//判断是否存在停止分配标志位
				if(TaskItemRedisDAO.isPauseAssign() == 1){
					continue;
				}
				
				//判断til是否为空或者cn为0
				int cn = countFreeRobot();
				List<AGVIOTaskItem> taskItems = new ArrayList<>();
				TaskItemRedisDAO.appendTaskItems(taskItems);
				if (taskItems.isEmpty() || cn == 0) {
					continue;
				}
				
				sendLSs(cn, taskItems);
			} catch (Exception e) {
				if(e instanceof InterruptedException) {
					break;
				}else {
					ErrorLogWritter.save(e.getClass().getSimpleName()+ ":" +e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public static void sendLSs(int cn, List<AGVIOTaskItem> taskItems) throws Exception {
		//获取第a个元素
		int a = 0;
		do{
			//获取对应item
			AGVIOTaskItem item = taskItems.get(a);
			//查询对应物料类型
			MaterialType materialType = MaterialType.dao.findById(item.getMaterialTypeId());
			//判断状态是否为0并且物料类型是否在架（未分配）
			if (item.getState() == 0 && materialType.getIsOnShelf()) {
				//发送LS
				LSSLHandler.sendLS(item, materialType);
				cn--;
			}
			a++;
		}while(cn != 0 && a != taskItems.size());
	}
	
	
	private static int countFreeRobot() {
		List<RobotBO> freeRobots = new ArrayList<>();
		for (RobotBO robot : RobotInfoRedisDAO.check()) {
			//筛选空闲或充电状态的处于启用中的叉车
			if((robot.getStatus() == 0 || robot.getStatus() == 4) && robot.getEnabled() == 2) {
				freeRobots.add(robot);
			}
		}
		return freeRobots.size();
	}
	
}
