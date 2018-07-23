package com.jimi.uw_server.controller;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jimi.uw_server.model.User;
import com.jimi.uw_server.service.LogService;
import com.jimi.uw_server.util.ResultUtil;
import com.jimi.uw_server.util.TokenBox;

/**
 * 日志控制层
 * @author HardyYao
 * @createTime 2018年6月8日
 */
public class LogController extends Controller {

	private static LogService logService = Enhancer.enhance(LogService.class);
	
	public static final String SESSION_KEY_LOGIN_USER = "loginUser";

	public void select(String table, Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		if (table.equals("action_log")) {
			renderJson(ResultUtil.succeed(logService.select(table, pageNo, pageSize, ascBy, descBy, filter)));
		} else if(table.equals("task_log")) {	// 查询「任务日志」
			renderJson(ResultUtil.succeed(logService.selectTaskLog(table, pageNo, pageSize, ascBy, descBy, filter)));
		} else if(table.equals("position_log")) {	// 查询「物料位置转移日志」
			renderJson(ResultUtil.succeed(logService.selectPositionLog(table, pageNo, pageSize, ascBy, descBy, filter)));
		}
		
	}
	
	
	public void writeIO(Integer taskId, String materialId, Integer quantity) {
		// 获取当前使用系统的用户名
		String tokenId = getPara(TokenBox.TOKEN_ID_KEY_NAME);
		User user = TokenBox.get(tokenId, SESSION_KEY_LOGIN_USER);
		if (logService.writeIO(taskId, materialId, quantity, user)) {
			renderJson(ResultUtil.succeed());
		} else {
			renderJson(ResultUtil.failed());
		}
		
	}
	
}
