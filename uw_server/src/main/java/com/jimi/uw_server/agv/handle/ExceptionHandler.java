package com.jimi.uw_server.agv.handle;

import com.jfinal.json.Json;
import com.jimi.uw_server.agv.dao.TaskItemRedisDAO;
import com.jimi.uw_server.agv.entity.bo.AGVIOTaskItem;
import com.jimi.uw_server.agv.entity.cmd.AGVLoadExceptionCmd;

/**
 * 异常处理器
 * <br>
 * <b>2018年7月13日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class ExceptionHandler {

	public static void handleLoadException(String message) {
		//把负载异常的条目回滚到状态0
		AGVLoadExceptionCmd loadExceptionCmd = Json.getJson().parse(message, AGVLoadExceptionCmd.class);
		String groupid = loadExceptionCmd.getMissiongroupid();
		for(AGVIOTaskItem item : TaskItemRedisDAO.getTaskItems()) {
			if(item.getGroupId().equals(groupid)) {
				TaskItemRedisDAO.updateTaskItemState(item, 0);
				break;
			}
		}
	}
	
}
