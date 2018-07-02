package com.jimi.uw_server.controller;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import com.jimi.uw_server.exception.ParameterException;
import com.jimi.uw_server.model.User;
import com.jimi.uw_server.service.UserService;
import com.jimi.uw_server.util.ResultUtil;
import com.jimi.uw_server.util.TokenBox;

public class UserController extends Controller {

	private UserService userService = Enhancer.enhance(UserService.class);
	
	public static final String USER_TABLE_NAME = "User";
	
	public static final String SESSION_KEY_LOGIN_USER = "loginUser";
	
	public void login(String uid, String password) {
		User user = userService.login(uid, password);
		//判断是否重复登录
		String tokenId = getPara(TokenBox.TOKEN_ID_KEY_NAME);
		if(tokenId != null) {
			User user2 = TokenBox.get(tokenId, SESSION_KEY_LOGIN_USER);
			if(user2 != null && user.getUid() == user2.getUid()) {
				throw new ParameterException("do not login again");
			}
		}

		user.put(TokenBox.TOKEN_ID_KEY_NAME, TokenBox.createTokenId());
		System.out.println("user: " + user.toString());
		TokenBox.put(TokenBox.createTokenId(), SESSION_KEY_LOGIN_USER, user);
		renderJson(ResultUtil.succeed(user));
	}

	public void checkLogined() {
		User user = TokenBox.get(getPara(TokenBox.TOKEN_ID_KEY_NAME), SESSION_KEY_LOGIN_USER);
		if(user != null) {
			renderJson(ResultUtil.succeed(user));
		}else {
			renderJson(ResultUtil.succeed("no user signed in"));
		}
	}

//	@Access({"SuperAdmin"})
	public void add(@Para("") User user) {
		if(userService.add(user)) {
			renderJson(ResultUtil.succeed());
		} else {
			renderJson(ResultUtil.failed());
		}
	}
	
//	@Access({"SuperAdmin"})
	public void update(@Para("") User user) {
		if(userService.update(user)) {
			renderJson(ResultUtil.succeed());
		} else {
			renderJson(ResultUtil.failed());
		}
	}
	
	public void select(String table, Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		table = "user";
		renderJson(ResultUtil.succeed(userService.select(table, pageNo, pageSize, ascBy, descBy, filter)));
	}
	
	public void getTypes(Integer pageNo, Integer pageSize) {
		renderJson(ResultUtil.succeed(userService.getTypes(pageNo, pageSize)));
	}
	
//	@Access({"SuperAdmin"})
	public void logout() {
		//判断是否未登录
		String tokenId = getPara(TokenBox.TOKEN_ID_KEY_NAME);
		User user = TokenBox.get(tokenId, SESSION_KEY_LOGIN_USER);
		if(user == null) {
			throw new ParameterException("do not need to logout when not login");
		}
		TokenBox.remove(tokenId);
		renderJson(ResultUtil.succeed());
	}
	
}
