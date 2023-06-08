package com.imooc.bilibili.domain;

import lombok.Data;

import java.util.Date;

/**
 * 描述: TODO
 */
@Data
public class FollowingGroup {

    private Long id;

    private Long userId;

    private String name;

    private String type;

    private Date createTime;

    private Date updateTime;

}
