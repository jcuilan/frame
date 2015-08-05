package com.spring.service.login;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.common.utils.security.MD5Encode;
import com.spring.Exception.CaptchaException;
import com.spring.Exception.XmlException;
import com.spring.model.login.Permission;
import com.spring.model.login.User;
import com.spring.model.login.UserRole;
import com.spring.utils.UsernamePasswordCaptchaToken;

/**
 * 用户登录授权service(shrioRealm)
 * @author ty
 * @date 2015年1月14日
 */
@Service
@DependsOn({"userMapper","permissionMapper"})
public class UserRealm extends AuthorizingRealm {

	@Autowired
	private UserService userService;
	
	@Autowired
	private PermissionService permissionService;

	/**
	 * 认证回调函数,登录时调用.
	 */
	@SuppressWarnings("unused")
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordCaptchaToken token = (UsernamePasswordCaptchaToken) authcToken; 
		User user=null;
		try {
			user = userService.getUser(token.getUsername());
		} catch (Exception e) {
			e.printStackTrace();
			throw new XmlException(e.getCause().getMessage());
		}
		
		
		if (user != null&&doCaptchaValidate(token)) {
			ShiroUser shiroUser=new ShiroUser(user.getId(), user.getLoginName(), user.getName());
			//设置用户session
			Session session =SecurityUtils.getSubject().getSession();
			session.setAttribute("user", user);
			session.setAttribute("shopId", 1);
			//生成用户菜单
			List<Permission> permissionlist; //用户拥有的资源
			List<Permission> parentlist = new ArrayList<Permission>(); //用户一级菜单对象
			if(user.getIsAdmin().equals("on")){ //管理员
				permissionlist = permissionService.getAllPermissions();
				parentlist = permissionService.getPermissionsByLevel(1);
			}else{
				permissionlist = permissionService.getPermissions(user.getId());
				parentlist = permissionService.getPermissionsByUserLevel(user.getId(), 1);
			}
			session.setAttribute("permissionlist", permissionlist);
			session.setAttribute("parentlist", parentlist);
			
			
			return new SimpleAuthenticationInfo(user,user.getPassword(), getName());
		} else {
			return null;
		}
	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		ShiroUser shiroUser = (ShiroUser) principals.getPrimaryPrincipal();
		User user = userService.getUser(shiroUser.loginName);
		
		//把principals放session中 key=userId value=principals
		SecurityUtils.getSubject().getSession().setAttribute(String.valueOf(user.getId()),SecurityUtils.getSubject().getPrincipals());
		
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		//赋予角色
		for(UserRole userRole:user.getUserRoles()){
			info.addRole(userRole.getId().toString());
		}
		//赋予权限
		for(Permission permission:permissionService.getPermissions(user.getId())){
			if(StringUtils.isNotBlank(permission.getUrl()))
			info.addStringPermission(permission.getUrl());
		}
		
		//设置登录次数、时间
		//userService.updateUserLogin(user);
		return info;
	}
	
	/**
	 * 验证码校验
	 * @param token
	 * @return boolean
	 */
	protected boolean doCaptchaValidate(UsernamePasswordCaptchaToken token)
	{
		String captcha = (String) SecurityUtils.getSubject().getSession().getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
		if (captcha != null &&!captcha.equalsIgnoreCase(token.getCaptcha())){
			throw new CaptchaException("验证码错误！");
		}
		return true;
	}
		

	/**
	 * 设定Password校验为自定义MD5加密方式
	 */
	@PostConstruct
	public void initCredentialsMatcher() {
		setCredentialsMatcher(new CustomCredentialsMatcher());
	}
	public class CustomCredentialsMatcher extends SimpleCredentialsMatcher{

		@Override
		public boolean doCredentialsMatch(AuthenticationToken token,
				AuthenticationInfo info) {
			
			UsernamePasswordCaptchaToken itoken = (UsernamePasswordCaptchaToken)token;
			Object tokenCreadentials = encrypt(String.valueOf(itoken.getPassword()));
			Object accountCredentials = getCredentials(info);
			
			return equals(tokenCreadentials,accountCredentials);
		}
		public String encrypt(String data){
			String Md5 = MD5Encode.getMD5(data,"UTF-8").toUpperCase();
			return Md5;
		}
	}

	/**
	 * 自定义Authentication对象，使得Subject除了携带用户的登录名外还可以携带更多信息.
	 */
	public static class ShiroUser implements Serializable {
		private static final long serialVersionUID = -1373760761780840081L;
		public Integer id;
		public String loginName;
		public String name;

		public ShiroUser(Integer id, String loginName, String name) {
			this.id = id;
			this.loginName = loginName;
			this.name = name;
		}

		public Integer getId(){
			return id;
		}

		public String getName() {
			return name;
		}

		/**
		 * 本函数输出将作为默认的<shiro:principal/>输出.
		 */
		@Override
		public String toString() {
			return loginName;
		}

		/**
		 * 重载hashCode,只计算loginName;
		 */
		@Override
		public int hashCode() {
			return Objects.hashCode(loginName);
		}

		/**
		 * 重载equals,只计算loginName;
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ShiroUser other = (ShiroUser) obj;
			if (loginName == null) {
				if (other.loginName != null) {
					return false;
				}
			} else if (!loginName.equals(other.loginName)) {
				return false;
			}
			return true;
		}
	}
	
	@Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }
 
}
