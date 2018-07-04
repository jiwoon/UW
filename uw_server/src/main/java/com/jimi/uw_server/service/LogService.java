package com.jimi.uw_server.service;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jimi.uw_server.exception.OperationException;
//import com.jfinal.aop.Enhancer;
import com.jimi.uw_server.service.base.SelectService;
//import com.jimi.uw_server.util.ResultUtil;

public class LogService extends SelectService {
	
	private static final String actionLogSelectSql = "SELECT id, ip, uid, action, time";
	
	private static final String actionLogNonSelectSql = "FROM action_log";
	
	public Object selectLog(String table, Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter) {
		if (table.equals("action_log")) {
			return Db.paginate(pageNo, pageSize, actionLogSelectSql, actionLogNonSelectSql);
		} else if(table.equals("task_log")) {
			
		} else if(table.equals("postion_log")){
			
		} else {
			throw new OperationException("请输入正确的日志表名！");
		}
		return null;
	}
	
}
