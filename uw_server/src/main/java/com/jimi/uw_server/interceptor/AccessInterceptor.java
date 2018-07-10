package com.jimi.uw_server.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jimi.uw_server.controller.UserController;
import com.jimi.uw_server.exception.AccessException;
import com.jimi.uw_server.model.User;
import com.jimi.uw_server.model.UserType;
import com.jimi.uw_server.util.TokenBox;


/**
 * UserType.permission语法结构：<br>
 * 分成两部分，即权限模式和受控接口列表，其中权限模式共有两种，分为allow和expect，前者允许该类型用户访问受控接口列表中的接口，后者则不允许。<br>
 * 受控接口列表共有两种写法，即枚举写法和通配符写法，枚举写法支持单独列出受控接口，使用逗号分开；通配符则表示所有接口都受控，使用星号表示
 * <br>
 * 如：allow:* 表示允许该类型用户访问所有接口<br>
 * expect:* 则表示不允许该类型用户访问所有接口<br>
 * allow:/manage/user/login,/manager/user/add 则只允许用户访问这两个接口
 * <br>
 * 权限不足异常，result：401
 * <br>
 * <b>2018年6月2日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AccessInterceptor implements Interceptor {

	
	@Override
	public void intercept(Invocation invocation) {
		 String token = invocation.getController().getPara(TokenBox.TOKEN_ID_KEY_NAME);
		 User user = TokenBox.get(token, UserController.SESSION_KEY_LOGIN_USER);
		 int userTypeId = 0;
		 if(user != null && user.getEnabled()) {
			 user = User.dao.findById(user.getUid());
			 userTypeId = user.getType();
		 }
		 //获取用户类型权限
		 UserType userType = UserType.dao.findById(userTypeId);
		 String permission = userType.getPermission();
		//获取方法名
		 String requestMethodName = invocation.getActionKey();
		 //解析权限字
		 String allowOrExpect = permission.split(":")[0];
		 String permissionMethodNames = permission.split(":")[1];
		 if(allowOrExpect.equals("allow")) {
			 if(permissionMethodNames.equals("*")) {
				 invocation.invoke();
			 }else {
				 for (String permissionMethodName : permissionMethodNames.split(",")) {
					if(permissionMethodName.equals(requestMethodName)) {
						invocation.invoke();
						return;
					}
				 }
				 throw new AccessException("access denied");
			 }
		 }else {
			 if(permissionMethodNames.equals("*")) {
				 throw new AccessException("access denied");
			 }else {
				 for (String permissionMethodName : permissionMethodNames.split(",")) {
					if(permissionMethodName.equals(requestMethodName)) {
						throw new AccessException("access denied");
					}
				 }
				 invocation.invoke();
			 }
		 }
		 
	}
}
