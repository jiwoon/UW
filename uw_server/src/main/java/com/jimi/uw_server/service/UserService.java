package com.jimi.uw_server.service;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jimi.uw_server.exception.OperationException;
import com.jimi.uw_server.model.User;
import com.jimi.uw_server.model.vo.UserVO;
import com.jimi.uw_server.service.base.SelectService;
import com.jimi.uw_server.service.entity.PagePaginate;

import cc.darhao.dautils.api.MD5Util;

/**
 * 用户业务层
 * <br>
 * <b>2018年5月29日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class UserService extends SelectService{
	
	private static SelectService selectService = Enhancer.enhance(SelectService.class);

	private static final String loginSql = "SELECT * "
			+ "FROM user WHERE uid = ? AND password = ?";
	
	private static final String uniqueCheckSql = "SELECT * FROM user WHERE uid = ?";
	
	private static final String userTypeSelectSql = "SELECT id,name";
	
	private static final String userTypeNonSelectSql = "FROM user_type";
	
	public User login(String uid, String password) {
		User user = User.dao.findFirst(loginSql, uid, MD5Util.MD5(password));
		if(user == null) {
			throw new OperationException("userId or password is not correct");
		} 
		if(!user.getEnabled()) {
			System.out.println("user.getEnabled(): " + user.getEnabled());
			throw new OperationException("用户" + user.getName() + "已被禁用！");
		}
		return user;
	}

	public boolean add(User user) {
		user.setEnabled(true);
		if(User.dao.find(uniqueCheckSql, user.getUid()).size() != 0) {
			throw new OperationException("用户" + user.getName() + "已存在！");
		}
		user.keep("uid","name","password","type", "enabled");
		user.setPassword(MD5Util.MD5(user.getPassword()));
		return user.save();
	}

	public boolean update(User user) {
		user.keep("uid","name","password","type","enabled");
		return user.update();
	}
	
	public Object select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter) {
		List<UserVO> userVO = new ArrayList<UserVO>();
		
		Page<Record> result = selectService.select("user", pageNo, pageSize, ascBy, descBy, filter);
		
		int totallyRow =  0;
		
		for (Record res : result.getList()) {
			UserVO u = new UserVO(res.get("uid"), res.get("password"), res.get("name"), res.get("type"), res.get("enabled"));
			userVO.add(u);
			totallyRow++;
		}
		
		PagePaginate pagePaginate = new PagePaginate();
		pagePaginate.setPageSize(pageSize);
		pagePaginate.setPageNumber(pageNo);
		pagePaginate.setTotalRow(totallyRow);
		
		pagePaginate.setList(userVO);
		
		return pagePaginate;
	}
	
	public Object getTypes(Integer pageNo, Integer pageSize) {
		return Db.paginate(pageNo, pageSize, userTypeSelectSql, userTypeNonSelectSql);
	}

}
