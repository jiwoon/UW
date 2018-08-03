package com.jimi.uw_server.config;

import java.io.File;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.template.Engine;
import com.jimi.uw_server.agv.socket.AGVMainSocket;
import com.jimi.uw_server.agv.socket.RobotInfoSocket;
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
import com.jimi.uw_server.util.ErrorLogWritter;
import com.jimi.uw_server.util.TokenBox;

/**
 * 全局配置
 *
 */
public class UwConfig extends JFinalConfig {

	@Override
	public void configConstant(Constants me) {
		me.setDevMode(false);
		me.setJsonFactory(new MixedJsonFactory());
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
		me.addGlobalServiceInterceptor(new Tx());
	}

	@Override
	public void configPlugin(Plugins me) {
		PropKit.use("properties.ini");
		//判断是否是生产环境
		DruidPlugin dp = null;
		RedisPlugin rp = null;
		if(isProductionEnvironment()) {
			dp = new DruidPlugin(PropKit.get("p_url"), PropKit.get("p_user"), PropKit.get("p_password"));
			rp = new RedisPlugin("uw", PropKit.get("p_redisIp"), PropKit.get("p_redisPassword"));
			System.out.println("System is in production envrionment");
		}else {
			dp = new DruidPlugin(PropKit.get("d_url"), PropKit.get("d_user"), PropKit.get("d_password"));
			rp = new RedisPlugin("uw", PropKit.get("d_redisIp"), PropKit.get("d_redisPassword"));
			System.out.println("System is in development envrionment");
		}
		me.add(dp);
		me.add(rp);
		//配置ORM
	    ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
	    arp.setDialect(new MysqlDialect());	// 用什么数据库，就设置什么Dialect
	    arp.setShowSql(false);
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
		try {
			TokenBox.start(PropKit.use("properties.ini").getInt("sessionTimeout"));
			if(isProductionEnvironment()) {
				AGVMainSocket.init(PropKit.use("properties.ini").get("p_agvServerURI"));
				RobotInfoSocket.init(PropKit.use("properties.ini").get("p_robotInfoURI"));
			}else {
				AGVMainSocket.init(PropKit.use("properties.ini").get("d_agvServerURI"));
				RobotInfoSocket.init(PropKit.use("properties.ini").get("d_robotInfoURI"));
			}
			System.out.println("Uw Server is Running now...");
		} catch (Exception e) {
			ErrorLogWritter.save(e.getClass().getSimpleName() + ":" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void beforeJFinalStop() {
		TokenBox.stop();
	}
	
	
	public static boolean isProductionEnvironment() {
		File[] roots = File.listRoots();
        for (int i=0; i < roots.length; i++) {
            if(new File(roots[i].toString() + "PRODUCTION_ENVIRONMENT_FLAG").exists()) {
            	return true;
            }
        }
        return false;
	}

}
