package com.imooc.bilibili.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface UserCoinDao {


    Integer getUserCoinAmount(Long userId);

    Integer updateVideoCoin(@Param("userId") Long userId, @Param("amount") Integer amount, @Param("updateTime") Date updateTime);
}
