package com.spring.service.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.utils.ConfigUtils;
import com.spring.dao.login.PermissionMapper;
import com.spring.model.login.Permission;

/**
 * 权限service
 * @author ty
 * @date 2015年1月13日
 */
@Service
@Transactional(readOnly=true)
public class PermissionService {
	
	@Autowired
	private PermissionMapper permissionDao;
	


	/**
	 * 查询所有资源
	 */
	public List<Permission> getAllPermissions(){
	    Integer rootId=	Integer.valueOf(ConfigUtils.config("resourceRootId"));
		return permissionDao.findPermissions(rootId);
	}
	/**
	 * 获取用户拥有的权限集合
	 * @param userId
	 * @return 结果集合
	 */
	public List<Permission> getPermissions(Integer userId){
		Integer rootId=	Integer.valueOf(ConfigUtils.config("resourceRootId"));
		return permissionDao.findPermissionsByuserId(userId,rootId);
	}
	/**
	 * 获取子权限
	 * @param userId
	 * @return 结果集合
	 */
	public List<Permission> getsonPermissions(Integer id){
		Integer rootId=	Integer.valueOf(ConfigUtils.config("resourceRootId"));
		return permissionDao.findPermissionsByParentId(id,rootId);
	}
	/**
	 * 获取用户拥有某一等级的资源
	 * @param userId
	 * @return 结果集合
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Permission> getPermissionsByUserLevel(Integer userId,Integer level){
		Integer rootId=	Integer.valueOf(ConfigUtils.config("resourceRootId"));
		HashMap param = new HashMap();
		param.put("userId", userId);
		param.put("level", level);
		param.put("rootId", rootId);
		return permissionDao.findPermissionsByUserLevel(param);
	}
	/**
	 * 获取某一等级的资源
	 * @param userId
	 * @return 结果集合
	 */
	public List<Permission> getPermissionsByLevel(Integer level){
		Integer rootId=	Integer.valueOf(ConfigUtils.config("resourceRootId"));
		return permissionDao.findPermissionsByLevel(level,rootId);
	}
	
	/**
	 * 根据资源ID从session中获取登录用户的下级子元素
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Permission> getSonResourceList(int resid){
		List<Permission> reslist = (List<Permission>) SecurityUtils.getSubject().getSession().getAttribute("permissionlist"); //用户所有资源
		/**********根据上层目录的类型和ID返回该目录的下级菜单********************/		
		List<Permission> returnReslist = new ArrayList<Permission>(); //返回本级目录下面的子元素
		if(reslist!=null&&reslist.size()>0){
			for(Permission resource:reslist){
				if(resource.getParentId()!=null){
					if(resource.getParentId()==resid){
						returnReslist.add(resource);
					}
				}				
			}
		}
		return returnReslist;
	}
	/**
	 * 生成目录树
	 * @param inResList
	 * @param 
	 * @return
	 */
	public List<Permission> getallResTreeList(List<Permission> inResList){
		for(Permission resource:inResList){ //循环下级目录，取出其中所有子元素
			if(resource.getType()!=null&&resource.getType().equals("1")){//如果子元素是节点 取出下面的子元素			
				resource.setSonResourceList(getallResTreeList(this.getSonResourceList(resource.getId())));
			}	
		}
		return inResList;
	}
}
