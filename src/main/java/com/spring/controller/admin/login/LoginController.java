package com.spring.controller.admin.login;


import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.common.utils.ConfigUtils;
import com.spring.service.login.PermissionService;

/**
 * 登录controller
 * @author ty
 * @date 2015年1月14日
 */
@Controller
@RequestMapping(value = "{adminPath}")
public class LoginController{
	@Autowired
	private PermissionService permissionService;
	/**
	 * 默认页面
	 * @return
	 */
	@RequestMapping(value="login",method = RequestMethod.GET)
	public String login(HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		if(subject.isAuthenticated()||subject.isRemembered()){
			return "redirect:"+ConfigUtils.config("adminPath")+"/admin.htm";
		}else{		
			return "login/login";
		}
		
	}

	/**
	 * 登录失败
	 * @param userName
	 * @param model
	 * @return
	 */
	@RequestMapping(value="login",method = RequestMethod.POST)
	public String fail(@RequestParam(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM) String userName, Model model) {
		model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, userName);
		return "login/login";
	}

	/**
	 * 登出
	 * @param userName
	 * @param model
	 * @return
	 */
	@RequestMapping(value="logout")
	public String logout(Model model) {
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		model.addAttribute("message", "您已安全退出");
		return "login/login";
	}
}
