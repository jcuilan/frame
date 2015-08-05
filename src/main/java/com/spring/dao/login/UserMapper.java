package com.spring.dao.login;

import com.spring.model.login.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    /**
     * 根据登录名称查询
     * @param loginName
     * @return
     */
    User findUniqueByLoginName(String loginName);
}