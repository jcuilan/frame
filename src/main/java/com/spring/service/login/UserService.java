package com.spring.service.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.dao.login.UserMapper;
import com.spring.model.login.User;

/**
 * 用户service
 * @author ty
 * @date 2015年1月13日
 */
@Service
@Transactional(readOnly = true)
public class UserService  {
	
	/**加密方法*/
	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;


	@Autowired
	private UserMapper userDao;



	
	/**
	 * 按登录名查询用户
	 * @param loginName
	 * @return 用户对象
	 */
	public User getUser(String loginName) {
		User user= userDao.findUniqueByLoginName(loginName);
		return user;
	}
	

}
