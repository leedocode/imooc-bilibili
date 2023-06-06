package com.imooc.bilibili.dao;

import com.imooc.bilibili.domain.User;
import com.imooc.bilibili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 描述: TODO
 */

@Mapper
public interface UserDao {


    User getUserByPhone(String phone);

    Integer addUser(User user);

    Integer addUserInfo(UserInfo userInfo);

    User getUserById(Long Id);

    UserInfo getUserInfoByUserId(Long userId);
}
