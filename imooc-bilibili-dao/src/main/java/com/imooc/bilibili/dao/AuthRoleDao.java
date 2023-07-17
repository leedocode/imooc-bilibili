package com.imooc.bilibili.dao;

import com.imooc.bilibili.domain.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 描述: 权限角色mapper
 */

@Mapper
public interface AuthRoleDao {

    AuthRole getRoleByCode(String code);
}
