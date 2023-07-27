package com.imooc.bilibili.domain;

import lombok.Data;

import java.util.Date;

/**
 * 描述: 视频投币实体类
 */
@Data
public class UserCoin {

    private Long id;

    private Long userId;

    private Integer amount;

    private Date createTime;

    private Date updateTime;
}
