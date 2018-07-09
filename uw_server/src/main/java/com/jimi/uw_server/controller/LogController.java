package com.jimi.uw_server.controller;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jimi.uw_server.service.LogService;
import com.jimi.uw_server.util.ResultUtil;

/**
 * 日志控制层
 * @author HardyYao
 * @createTime 2018年6月8日
 */
public class LogController extends Controller {

	private static LogService logService = Enhancer.enhance(LogService.class);
	
	public void select(String table, Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		renderJson(ResultUtil.succeed(logService.selectLog(table, pageNo, pageSize, ascBy, descBy, filter)));
	}
	
}
