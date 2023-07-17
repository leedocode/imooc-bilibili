package com.imooc.bilibili.service;

import com.imooc.bilibili.dao.UserRoleDao;
import com.imooc.bilibili.domain.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述: TODO
 */
@Service
public class UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    public List<UserRole> getUserRoleByUserId(Long userId) {
        return userRoleDao.getUserRoleByUserId(userId);
    }

    public void addUserRole(UserRole userRole) {
        userRoleDao.addUserRole(userRole);
    }
}
