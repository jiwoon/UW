package com.jimi.uw_server.controller;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jimi.uw_server.service.LogService;
import com.jimi.uw_server.util.ResultUtil;

public class LogController extends Controller {

	private static LogService logService = Enhancer.enhance(LogService.class);
	
	public void select(String table, Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		renderJson(ResultUtil.succeed(logService.selectLog(table, pageNo, pageSize, ascBy, descBy, filter)));
	}
	
}
