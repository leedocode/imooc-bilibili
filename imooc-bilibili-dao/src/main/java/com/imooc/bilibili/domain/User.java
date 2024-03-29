package com.imooc.bilibili.domain;

import lombok.Data;

import java.util.Date;
import java.util.Stack;

/**
 * 描述: TODO
 */

@Data
public class User {

    private Long id;

    private String nickName;
    private String phone;

    private String email;

    private String password;

    private String salt;

    private Date createTime;

    private Date updateTime;

    private UserInfo userInfo;
}

