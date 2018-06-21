package com.jimi.uw_server.config;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.json.FastJsonFactory;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;
import com.jimi.uw_server.agv.AGVCommunicator;
import com.jimi.uw_server.controller.LogController;
import com.jimi.uw_server.controller.MaterialController;
import com.jimi.uw_server.controller.RobotController;
import com.jimi.uw_server.controller.TaskController;
import com.jimi.uw_server.controller.UserController;
import com.jimi.uw_server.interceptor.AccessInterceptor;
import com.jimi.uw_server.interceptor.ActionLogInterceptor;
import com.jimi.uw_server.interceptor.CORSInterceptor;
import com.jimi.uw_server.interceptor.ErrorLogInterceptor;
import com.jimi.uw_server.model.MappingKit;
import com.jimi.uw_server.util.TokenBox;

/**
 * 全局配置
 *
 */
public class UwConfig extends JFinalConfig {

	@Override
	public void configConstant(Constants me) {
		me.setDevMode(true);
		me.setJsonFactory(new FastJsonFactory());
	}

	@Override
	public void configEngine(Engine me) {
	}

	@Override
	public void configHandler(Handlers me) {
	}

	@Override
	public void configInterceptor(Interceptors me) {
		me.addGlobalActionInterceptor(new ErrorLogInterceptor());
		me.addGlobalActionInterceptor(new CORSInterceptor());
		me.addGlobalActionInterceptor(new AccessInterceptor());
		me.addGlobalActionInterceptor(new ActionLogInterceptor());
		me.addGlobalActionInterceptor(new Tx());
	}

	@Override
	public void configPlugin(Plugins me) {
		PropKit.use("properties.ini");
		//配置数据连接池
		DruidPlugin dp = new DruidPlugin(PropKit.get("url"), PropKit.get("user"), PropKit.get("password"));
		me.add(dp);
		//配置ORM
	    ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
	    arp.setDialect(new MysqlDialect());	// 用什么数据库，就设置什么Dialect
	    arp.setShowSql(true);
	    MappingKit.mapping(arp);
	    me.add(arp);
	}

	@Override
	public void configRoute(Routes me) {
		me.add("/log", LogController.class);
		me.add("/task", TaskController.class);
		me.add("/manage/robot", RobotController.class);
		me.add("/manage/material", MaterialController.class);
		me.add("/manage/user", UserController.class);
	}

	@Override
	public void afterJFinalStart() {
		TokenBox.start(PropKit.use("properties.ini").getInt("sessionTimeout"));
		AGVCommunicator.connect(PropKit.use("properties.ini").get("AGVServerURI"));
		System.out.println("Uw Server is Running now...");
	}
	
	
	@Override
	public void beforeJFinalStop() {
		TokenBox.stop();
	}
	

}
