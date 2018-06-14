package com.jimi.uw_server.interceptor;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.json.Json;
import com.jimi.uw_server.annotation.Log;
import com.jimi.uw_server.controller.UserController;
import com.jimi.uw_server.model.ActionLog;
import com.jimi.uw_server.util.TokenBox;


/**
 * 操作日志拦截器，本拦截器会对带有@Log注解的方法进行日志记录，记录的详细信息为Log中的值，其中变量<br>
 * 使用<b>{变量}</b>表示，变量名和被注解的方法参数名必须一致，值为参数值。举个例子：<br><br>
 * @Log("获取了用户名为{userId}的用户")<br>
 * public void getUser(String userId){ ... }<br>
 * <br>
 * 如果传入参数值为"bobo"，则数据库的详细信息为"获取了用户名为bobo的用户"<br>
 * <br>
 * <b>2018年6月14日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class ActionLogInterceptor implements Interceptor {

	private static final String REGEX = "{[a-zA-Z0-9]+}";
	
	@Override
	public void intercept(Invocation invocation) {
		Log log = invocation.getMethod().getAnnotation(Log.class);
		Controller controller = invocation.getController();
		//如果存在@Log注解，则进行日志记录
		if(log != null) {
			//匹配参数并替换值
			Matcher matcher = Pattern.compile(REGEX).matcher(log.value());
			StringBuffer sb = new StringBuffer();  
			while(matcher.find()) {
				String a = matcher.group();
				a = a.substring(1, a.length() - 1);
				String value = controller.getPara(a);
				matcher.appendReplacement(sb, value);
			}
			matcher.appendTail(sb);
			//日志插入
			HttpServletRequest request = controller.getRequest();
			String url = request.getRequestURL().toString();
			String ip = request.getRemoteAddr();
			String parameters = Json.getJson().toJson(request.getParameterMap());
			ActionLog actionLog = new ActionLog();
			actionLog.setIp(ip);
			actionLog.setParameters(parameters);
			actionLog.setTime(new Date());
			actionLog.setUrl(url);
			actionLog.setAction(sb.toString());
			actionLog.setUid(TokenBox.get(controller.getPara("#TOKEN#"), UserController.SESSION_KEY_LOGIN_USER));
			actionLog.save();
		}
		invocation.invoke();
	}
}
