package com.jimi.uw_server.controller;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jimi.uw_server.annotation.Log;
import com.jimi.uw_server.exception.OperationException;
import com.jimi.uw_server.exception.ParameterException;
import com.jimi.uw_server.model.User;
import com.jimi.uw_server.service.UserService;
import com.jimi.uw_server.util.ResultUtil;
import com.jimi.uw_server.util.TokenBox;

/**
 * 用户控制层
 * @author HardyYao
 * @createTime 2018年6月8日
 */
public class UserController extends Controller {

	private UserService userService = Enhancer.enhance(UserService.class);

	public static final String SESSION_KEY_LOGIN_USER = "loginUser";


	// 登录
	@Log("用户名为{uid}的用户请求登录")
	public void login(String uid, String password) {
		User user = userService.login(uid, password);
		//判断是否重复登录
		String tokenId = getPara(TokenBox.TOKEN_ID_KEY_NAME);
		if(tokenId != null) {
			User user2 = TokenBox.get(tokenId, SESSION_KEY_LOGIN_USER);
			if(user2 != null && user.getUid().equals(user2.getUid())) {
				throw new ParameterException("do not login again");
			}
		}
		tokenId = TokenBox.createTokenId();
		user.put(TokenBox.TOKEN_ID_KEY_NAME, tokenId);
		TokenBox.put(tokenId, SESSION_KEY_LOGIN_USER, user);
		renderJson(ResultUtil.succeed(user));
	}


	// 检查登录
	public void checkLogined() {
		User user = TokenBox.get(getPara(TokenBox.TOKEN_ID_KEY_NAME), SESSION_KEY_LOGIN_USER);
		if(user != null) {
			renderJson(ResultUtil.succeed(user));
		}else {
			throw new OperationException("no user signed in");
		}
	}


	// 添加新用户
	@Log("添加了用户名为{uid}的用户,用户姓名为{name},用户类型为{type}")
	public void add(String uid, String name, String password, Integer type) {
		if(userService.add(uid, name, password, type)) {
			renderJson(ResultUtil.succeed());
		} else {
			throw new OperationException("请完善用户信息！");
		}
	}


	// 更新用户信息
	@Log("更新了用户{uid}的信息,更新后的用户姓名为{name},用户类型为type{}")
	public void update(String uid, String name, String password, Boolean enabled, Integer type) {
		if(userService.update(uid, name, password, enabled, type)) {
			renderJson(ResultUtil.succeed());
			User user = User.dao.findById(uid);
			if (!user.getEnabled()) {
				String tokenId = getPara(TokenBox.TOKEN_ID_KEY_NAME);
				User user1 = TokenBox.get(tokenId, SESSION_KEY_LOGIN_USER);
				if (user.getUid().equals(user1.getUid())) {
					logout();
				} else {
					TokenBox.remove(tokenId);
				}
			}
		} else {
			renderJson(ResultUtil.failed());
		}
	}


	// 查询所有用户信息
	public void select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		renderJson(ResultUtil.succeed(userService.select(pageNo, pageSize, ascBy, descBy, filter)));
	}
	

	// 获取用户类型
	public void getTypes() {
		renderJson(ResultUtil.succeed(userService.getTypes()));
	}


	// 退出登录
	public void logout() {
		//判断是否未登录
		String tokenId = getPara(TokenBox.TOKEN_ID_KEY_NAME);
		User user = TokenBox.get(tokenId, SESSION_KEY_LOGIN_USER);
		if(user == null) {
			throw new ParameterException("没有用户登录，不需要退出登录！");
		}
		TokenBox.remove(tokenId);
		renderJson(ResultUtil.succeed());
	}
	
}