package com.jimi.uw_server.service;

import com.jfinal.plugin.activerecord.Db;
import com.jimi.uw_server.exception.OperationException;
import com.jimi.uw_server.model.User;
import com.jimi.uw_server.service.base.SelectService;

/**
 * 用户业务层
 * <br>
 * <b>2018年5月29日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class UserService extends SelectService{

	private static final String loginSql = "SELECT * "
			+ "FROM user WHERE uid = ? AND password = ?";
//	private static final String userTypeSql = "SELECT * FROM user_type WHERE name = ?";
	private static final String uniqueCheckSql = "SELECT * FROM user WHERE uid = ?";
	private static final String userTypeSelectSql = "SELECT id,name";
	private static final String userTypeNonSelectSql = "FROM user_type";
	
	public User login(String uid, String password) {
		User user = User.dao.findFirst(loginSql, uid, password);
		if(user == null) {
			throw new OperationException("userName or password is not correct");
		} 
		if(!user.getEnabled()) {
			System.out.println("user.getEnabled(): " + user.getEnabled());
			throw new OperationException("this user is disabled");
		}
//		user.update();
		return user;
	}

	public boolean add(User user) {
		// 可以取到值，但是报错：Data too long for column 'password'
		user.setEnabled(true);
		if(User.dao.find(uniqueCheckSql, user.getUid()).size() != 0) {
			throw new OperationException("user is already exist");
		}
//		user.keep("uid","name","password","type", "enabled");
//		user.setPassword(MD5Util.MD5(user.getPassword()));
		return user.save();
	}
	
	public boolean update(User user) {
//		checkUserType(user);
		user.keep("uid","name","password","type","enabled");
		return user.update();
	}
	
	public Object getTypes(Integer pageNo, Integer pageSize) {
		return Db.paginate(pageNo, pageSize, userTypeSelectSql, userTypeNonSelectSql);
	}

}
