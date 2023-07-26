package com.imooc.bilibili.domain;

import lombok.Data;

import java.util.Date;

/**
 * 描述: 视频点赞实体类
 */

@Data
public class VideoLike {

    private Long id;

    private Long userId;

    private Long videoId;

    private Date createTime;
}
