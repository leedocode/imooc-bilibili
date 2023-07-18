package com.imooc.bilibili.dao;

import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.domain.User;
import com.imooc.bilibili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 描述: TODO
 */

@Mapper
public interface UserDao {


    User getUserByPhone(String phone);

    Integer addUser(User user);

    Integer addUserInfo(UserInfo userInfo);

    User getUserById(Long id);

    UserInfo getUserInfoByUserId(Long userId);

    Integer updateUserByPrimaryKeySelective(User user);

    Integer updateUserInfo(UserInfo userInfo);

    List<UserInfo> getUserInfoByUserIds(Set<Long> userIdSet);

    Integer pageCountUserInfos(Map<String,Object> params);

    List<UserInfo> pageListUserInfos(Map<String,Object> params);

    Integer deleteRefreshToken(@Param("refreshToken") String refreshToken, @Param("userId") Long userId);

    Integer addRefreshToken(@Param("refreshToken") String refreshToken, @Param("userId") Long userId, @Param("createTime") Date createTime);
}
