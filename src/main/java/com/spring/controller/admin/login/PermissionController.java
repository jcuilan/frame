package com.spring.controller.admin.login;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.spring.model.login.Permission;
import com.spring.service.login.PermissionService;


/**
 * 权限controller
 * @author ty
 * @date 2015年1月13日
 */
@Controller
@RequestMapping("login/permission")
public class PermissionController{
	
	@Autowired
	private PermissionService permissionService;
	

	/**
	 * 获取子菜单树
	 */
	@RequestMapping(value = "menu/getSonPermissionList", method = RequestMethod.GET)
	public String getSonPermissionList(String id,Model model) {
		List<Permission> sonPermissionList= new ArrayList<Permission>();
		sonPermissionList=permissionService.getallResTreeList(permissionService.getSonResourceList(Integer.parseInt(id)));	
		model.addAttribute("sonPermissionList",sonPermissionList);
		return "login/menu";
	}
	
}
