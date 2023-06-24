package com.imooc.bilibili.dao;

import com.imooc.bilibili.domain.UserMoment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 描述: TODO
 */

@Mapper
public interface UserMomentsDao {

    Integer addUserMoments(UserMoment userMoment);
}
