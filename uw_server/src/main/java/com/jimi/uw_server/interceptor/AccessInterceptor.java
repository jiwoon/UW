package com.jimi.uw_server.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jimi.uw_server.annotation.Access;
import com.jimi.uw_server.controller.UserController;
import com.jimi.uw_server.exception.AccessException;
import com.jimi.uw_server.model.User;
import com.jimi.uw_server.util.TokenBox;


/**
 * 权限不足异常，result：401
 * <br>
 * <b>2018年6月2日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AccessInterceptor implements Interceptor {

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public void intercept(Invocation invocation) {
		 Access access = invocation.getMethod().getAnnotation(Access.class);
		 if(access == null) {
			 invocation.invoke();
			 return;
		 }
		 String token = invocation.getController().getPara(TokenBox.TOKEN_ID_KEY_NAME);
		 User user = TokenBox.get(token, UserController.SESSION_KEY_LOGIN_USER);
		 if(user == null) {
			 throw new AccessException("not logined");
		 }
		 if(!user.getEnabled()) {
			 throw new AccessException("the user is disabled");
		 }
		 String[] accessUserTypes = access.value();
		 for (String userType : accessUserTypes) {
			if(userType.equals(user.getType())){
				invocation.invoke();
				return;
			}
		}
		throw new AccessException("access denied");
	}
}
