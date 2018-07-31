package com.jimi.uw_server.agv.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.json.Json;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jimi.uw_server.model.bo.RobotBO;

/**
 * AGV任务条目Redis数据访问对象
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class RobotInfoRedisDAO {

	private static Cache cache = Redis.use();
	

	/**
	 * 更新机器实时数据到Redis
	 */
	public synchronized static void update(List<RobotBO> robotBOs) {
		cache.del("robot");
		for (RobotBO agvRobot : robotBOs) {
			cache.lpush("robot", Json.getJson().toJson(agvRobot));
		}
	}
	
	
	/**
	 * 获取机器实时数据
	 */
	public synchronized static List<RobotBO> check(){
		List<RobotBO> robotBOs = new ArrayList<>();
		List<String> robots = cache.lrange("robot", 0, -1);
		for (String robot : robots) {
			robotBOs.add(Json.getJson().parse(robot, RobotBO.class));
		}
		return robotBOs;
	}
	
	
	/**
	 * 设置某台机器的负载异常为真
	 */
	public synchronized static void setloadException(int id) {
		List<RobotBO> robots = RobotInfoRedisDAO.check();
		for (RobotBO robotBO : robots) {
			if(robotBO.getId().intValue() == id) {
				robotBO.setLoadException(true);
				break;
			}
		}
		RobotInfoRedisDAO.update(robots);
	}
	
	
	/**
	 * 清除全部负载异常
	 */
	public synchronized static void clearLoadException() {
		List<RobotBO> robots = RobotInfoRedisDAO.check();
		for (RobotBO robot : robots) {
			robot.setLoadException(false);
		}
		RobotInfoRedisDAO.update(robots);
	}
	
}
