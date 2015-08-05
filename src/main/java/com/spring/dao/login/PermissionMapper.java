package com.spring.dao.login;


import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spring.model.login.Permission;

public interface PermissionMapper {
  

    Permission selectByPrimaryKey(Integer id);
    
    List<Permission> findPermissions(@Param("rootId") Integer rootId);  
    
    List<Permission> findPermissionsByuserId(@Param("userId") Integer userId,@Param("rootId") Integer rootId);
    
    List<Permission> findPermissionsByParentId(@Param("parentId") Integer parentId,@Param("rootId") Integer rootId);   
    
    @SuppressWarnings("rawtypes")
    
	List<Permission> findPermissionsByUserLevel(HashMap param); 
    
    List<Permission> findPermissionsByLevel(@Param("level") Integer level,@Param("rootId") Integer rootId);
  
}