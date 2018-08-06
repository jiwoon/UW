package com.jimi.uw_server.service;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jimi.uw_server.exception.OperationException;
import com.jimi.uw_server.model.User;
import com.jimi.uw_server.model.UserType;
import com.jimi.uw_server.model.vo.UserVO;
import com.jimi.uw_server.service.base.SelectService;
import com.jimi.uw_server.service.entity.PagePaginate;

import cc.darhao.dautils.api.MD5Util;

/**
 * 用户业务层
 * @author HardyYao
 * @createTime 2018年6月8日
 */
public class UserService extends SelectService{

	private static SelectService selectService = Enhancer.enhance(SelectService.class);

	private static final String LOGIN_SQL = "SELECT * FROM user WHERE uid = ? AND password = ?";

	private static final String UNIQUE_USER_CHECK_SQL = "SELECT * FROM user WHERE uid = ?";

	private static final String GET_USER_TYPE_SQL = "SELECT id,name FROM user_type WHERE id > 0";


	public User login(String uid, String password) {
		User user = User.dao.findFirst(LOGIN_SQL, uid, MD5Util.MD5(password));
		if(user == null) {
			throw new OperationException("用户名或密码不正确！");
		} 
		if(!user.getEnabled()) {
			throw new OperationException("用户" + user.getName() + "已被禁用！");
		}
		return user;
	}


	public boolean add(String uid, String name, String password, Integer type) {
		if (uid == null || name == null || password == null || type == null) {
			return false;
		}
		if(User.dao.find(UNIQUE_USER_CHECK_SQL, uid).size() != 0) {
			throw new OperationException("用户" + uid + "已存在！");
		}
		User user = new User();
		user.setUid(uid);
		user.setName(name);
		user.setPassword(MD5Util.MD5(password));
		user.setEnabled(true);
		user.setType(type);
		return user.save();
	}


	public boolean update(String uid, String name, String password, Boolean enabled, Integer type) {
		User user = User.dao.findById(uid);
		user.setUid(uid);
		if (!(name == null)) {
			user.setName(name);
		}
		if (!(password == null)) {
			user.setPassword(MD5Util.MD5(password));
		}
		if (!(enabled == null)) {
			user.setEnabled(enabled);
		}
		if (!(type == null)) {
			user.setType(type);
		}
		return user.update();
	}


	public Object select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter) {
		Page<Record> result = selectService.select("user", pageNo, pageSize, ascBy, descBy, filter);
		List<UserVO> userVOs = new ArrayList<UserVO>();
		for (Record res : result.getList()) {
			UserVO u = new UserVO(res.get("uid"), res.get("password"), res.get("name"), res.get("type"), res.get("enabled"));
			userVOs.add(u);
		}

		PagePaginate pagePaginate = new PagePaginate();
		pagePaginate.setPageSize(pageSize);
		pagePaginate.setPageNumber(pageNo);
		pagePaginate.setTotalRow(result.getTotalRow());
		pagePaginate.setList(userVOs);

		return pagePaginate;
	}


	public List<UserType> getTypes() {
		List<UserType> userTypes = new ArrayList<UserType>();
		userTypes = UserType.dao.find(GET_USER_TYPE_SQL);
		return userTypes;
	}

}
