package com.jimi.uw_server.service;

//import java.util.Date;

import com.jimi.uw_server.exception.OperationException;
import com.jimi.uw_server.exception.ParameterException;
//import com.jimi.uw_server.model.Material;
import com.jimi.uw_server.model.User;
import com.jimi.uw_server.model.UserType;
import com.jimi.uw_server.service.base.SelectService;

import cc.darhao.dautils.api.MD5Util;

/**
 * 鐢ㄦ埛涓氬姟灞�
 * <br>
 * <b>2018骞�5鏈�29鏃�</b>
 * @author 娌唺宸ヤ綔瀹� <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class UserService extends SelectService{
	
	private static final String loginSql = "SELECT * FROM user WHERE uid = ? AND password = ?";
	private static final String userTypeSql = "SELECT * FROM user_type WHERE name = ?";
	private static final String uniqueCheckSql = "SELECT * FROM user WHERE uid = ?";
	
	public User login(String userName, String password) {
		User user = User.dao.findFirst(loginSql, userName, MD5Util.MD5(password));
		if(user == null) {
			throw new OperationException("userName or password is not correct");
		}
		if(!user.getEnabled()) {
			throw new OperationException("this user is disabled");
		}
//		user.setLoginTime(new Date());
		user.update();
		return user;
	}
	
	
	public boolean add(User user) {
		checkUserType(user);
		if(User.dao.find(uniqueCheckSql, user.getUid()).size() != 0) {
			throw new OperationException("user is already exist");
		}
		user.keep("UserName","UserDes","UserPwd","UserType","UserTestPlan");
		user.setPassword(MD5Util.MD5(user.getPassword()));
		return user.save();
	}
	
	
	public boolean update(User user) {
		checkUserType(user);
		user.keep("UserId","UserDes","UserType","UserTestPlan","InService");
		return user.update();
	}


//	checkUserTypeAndTestPlan
	private void checkUserType(User user) {
		Integer userType =  user.getType();
		if(UserType.dao.find(userTypeSql, userType).size() == 0) {
			throw new ParameterException("user type not found");
		}
		/*String userTestPlan = user.getUserTestPlan();
		if(GpsTestplan.dao.findById(userTestPlan) == null) {
			throw new ParameterException("user test plan not found");
		}*/
	}
}
