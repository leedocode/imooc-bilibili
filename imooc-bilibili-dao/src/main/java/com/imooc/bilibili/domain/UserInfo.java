package com.imooc.bilibili.domain;


import lombok.Data;

import java.util.Date;

/**
 * 描述: TODO
 */

@Data
public class UserInfo {

    private Long id;

    private Long userId;

    private String nick;

    private String avatar;

    private String sign;

    private String gender;

    private String birth;

    private Boolean followed;

    private Date CreateTime;

    private Date updateTime;
}
