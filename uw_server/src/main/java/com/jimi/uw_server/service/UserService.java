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

	private static final String loginSql = "SELECT * "
			+ "FROM user WHERE uid = ? AND password = ?";

	private static final String uniqueCheckSql = "SELECT * FROM user WHERE uid = ?";

	private static final String getUserTypeSql = "SELECT id,name FROM user_type";


	public User login(String uid, String password) {
		User user = User.dao.findFirst(loginSql, uid, MD5Util.MD5(password));
		if(user == null) {
			throw new OperationException("用户名或密码不正确！");
		} 
		if(!user.getEnabled()) {
			throw new OperationException("用户" + user.getName() + "已被禁用！");
		}
		return user;
	}


	public boolean add(User user) {
		if (user.getUid() == null || user.getPassword() == null || user.getName() == null || user.getType() == null) {
			return false;
		}
		user.setEnabled(true);
		if(User.dao.find(uniqueCheckSql, user.getUid()).size() != 0) {
			throw new OperationException("用户" + user.getUid() + "已存在！");
		}
		user.keep("uid","name","password", "enabled","type");
		user.setPassword(MD5Util.MD5(user.getPassword()));
		return user.save();
	}


	public boolean update(User user) {
		// 更新用户信息时，如果修改了密码，需要对密码进行加密
		if (!(user.getPassword() == null)) {
			user.setPassword(MD5Util.MD5(user.getPassword()));
		}
		user.keep("uid","name","password", "enabled","type");
		return user.update();
	}


	public Object select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter) {
		if (filter != null) {
			if (filter.contains("enabled")) {
				filter = filter.replace("true", "1");
			}
		}
		
		List<UserVO> userVO = new ArrayList<UserVO>();
		Page<Record> result = selectService.select("user", pageNo, pageSize, ascBy, descBy, filter);

		int totallyRow =  result.getTotalRow();
		for (Record res : result.getList()) {
			UserVO u = new UserVO(res.get("uid"), res.get("password"), res.get("name"), res.get("type"), res.get("enabled"));
			userVO.add(u);
		}

		PagePaginate pagePaginate = new PagePaginate();
		pagePaginate.setPageSize(pageSize);
		pagePaginate.setPageNumber(pageNo);
		pagePaginate.setTotalRow(totallyRow);

		pagePaginate.setList(userVO);

		return pagePaginate;
	}


	public List<UserType> getTypes() {
		List<UserType> userTypes = new ArrayList<UserType>();
		userTypes = UserType.dao.find(getUserTypeSql);
		return userTypes;
	}

}
