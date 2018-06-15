package com.jimi.uw_server.controller;

//import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
//import com.jimi.uw_server.service.UserService;

public class UserController extends Controller {

//	private UserService userService = Enhancer.enhance(UserService.class);
	
	public static final String USER_TABLE_NAME = "Gps_User";
	
	public static final String SESSION_KEY_LOGIN_USER = "loginUser";
	
}
