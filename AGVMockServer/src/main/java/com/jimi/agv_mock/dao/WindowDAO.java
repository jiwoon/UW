package com.jimi.agv_mock.dao;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;

/**
 * 仓口的数据访问层
 * <br>
 * <b>2018年7月31日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class WindowDAO {

	private static final String URL = "jdbc:mysql://localhost:3306/uw?characterEncoding=utf8";
	private static final String USER = "root";
	private static final String PASSWORD = "newttl!@#$1234";
	
	private static final String GET_WINDOW_BY_TASK_ID = "SELECT * FROM window WHERE task_id = ?";
	
	public static void init() {
		DruidPlugin dp = new DruidPlugin(URL, USER, PASSWORD);
		dp.start();
		ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
	    arp.setDialect(new MysqlDialect());
	    arp.start();
	}
	
	
	public static int getWindowId(int taskId) {
		Window window = Window.dao.findFirst(GET_WINDOW_BY_TASK_ID, taskId);
		return window.getId();
	}
}
