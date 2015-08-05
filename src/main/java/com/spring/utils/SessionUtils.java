package com.spring.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

import com.spring.model.login.User;
import com.spring.service.login.UserRealm.ShiroUser;





public class SessionUtils {
	/**
	 * 获取当前用户对象shiro
	 * @return shirouser
	 */
	public static ShiroUser getCurrentShiroUser(){
		ShiroUser user=(ShiroUser) SecurityUtils.getSubject().getPrincipal();
		return user;
	}
	
	/**
	 * 获取当前用户session
	 * @return session
	 */
	public static Session getSession(){
		Session session =SecurityUtils.getSubject().getSession();
		return session;
	}
	
	/**
	 * 获取当前用户httpsession
	 * @return httpsession
	 */
	public static Session getHttpSession(){
		Session session =SecurityUtils.getSubject().getSession();
		return session;
	}
	
	/**
	 * 获取当前用户对象
	 * @return user
	 */
	public static User getCurrentUser(){
		Session session =SecurityUtils.getSubject().getSession();
		if(null!=session){
			return (User) session.getAttribute("user");
		}else{
			return null;
		}
		
	}
	
	/**
	 * 获取当前用户的shopid
	 * @return httpsession
	 */
	public static Integer getShopId(){
		Session session =SecurityUtils.getSubject().getSession();
		if(null!=session){
			return (Integer) session.getAttribute("shopId");
		}else{
			return null;
		}
	}
}
 