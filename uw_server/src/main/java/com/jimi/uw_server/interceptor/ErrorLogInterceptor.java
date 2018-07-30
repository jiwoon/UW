package com.jimi.uw_server.interceptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jimi.uw_server.model.ErrorLog;
import com.jimi.uw_server.util.ResultUtil;

import cc.darhao.dautils.api.ResourcesUtil;


/**
 * 错误Logger拦截器
 * <br>
 * <b>2018年5月29日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class ErrorLogInterceptor implements Interceptor {

	private static Logger logger;
	
	static {
		try(InputStream inputStream = ResourcesUtil.getResourceAsStream("log4j.properties")){
			PropertyConfigurator.configure(inputStream);
			logger = LogManager.getRootLogger();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void intercept(Invocation invocation) {
		try {
			invocation.invoke();
		}catch (Exception e) {
			int result = getResultCode(e);
			e.printStackTrace();
			ErrorLog errorLog = new ErrorLog();
			errorLog.setTime(new Date());
			errorLog.setMessage(e.getClass().getSimpleName() + ":" + e.getMessage());
			errorLog.save();
			logger.error(e.getClass().getSimpleName() + ":" + e.getMessage());
			invocation.getController().renderJson(ResultUtil.failed(result, e.getMessage()));
		}
	}

	
	/**
	 * 根据异常类获取返回码
	 */
	public static int getResultCode(Exception e) {
		int result;
		switch (e.getClass().getSimpleName()) {
		case "AccessException":
			result = 401;
			break;
		case "ParameterException":
			result = 400;			
			break;
		case "OperationException":
			result = 412;
			break;
		default:
			result = 500;
			break;
		}
		return result;
	}

}
