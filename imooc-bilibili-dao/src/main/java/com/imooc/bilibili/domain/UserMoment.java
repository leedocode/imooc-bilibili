package com.imooc.bilibili.domain;

import lombok.Data;

import java.util.Date;

/**
 * 描述: TODO
 */

@Data
public class UserMoment {

    private Long id;

    private Long userId;

    private String type;

    private Long contentId;

    private Date createTime;

    private Date updateTime;

}
