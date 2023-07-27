package com.imooc.bilibili.service;

import com.imooc.bilibili.dao.UserCoinDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 描述: 用户所持硬币服务类
 */

@Service
public class UserCoinService {

    @Autowired
    UserCoinDao userCoinDao;

    public Integer getUserCoinAmount(Long userId) {
        return userCoinDao.getUserCoinAmount(userId);
    }

    public void updateVideoCoin(Long userId, Integer amount) {
        userCoinDao.updateVideoCoin(userId, amount, new Date());
    }
}
