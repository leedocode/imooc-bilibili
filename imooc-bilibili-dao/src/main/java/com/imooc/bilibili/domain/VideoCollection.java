package com.imooc.bilibili.domain;

import lombok.Data;

import java.util.Date;

/**
 * 描述: TODO
 */
@Data
public class VideoCollection {
    private Long id;

    private Long userId;

    private Long videoId;

    private Long groupId;

    private Date createTime;
}
