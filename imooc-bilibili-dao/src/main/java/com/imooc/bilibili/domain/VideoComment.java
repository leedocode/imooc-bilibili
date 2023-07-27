package com.imooc.bilibili.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 描述: 视频评论实体类
 */

@Data
public class VideoComment {

    private Long id;

    private Long userId;

    private String comment;

    private Long videoId;

    private Long replyUserId;

    private Long rootId;

    private Date createTime;

    private Date updateTime;

    private List<VideoComment> childCommentList;

    private UserInfo userInfo;

    private UserInfo replyUserInfo;
}
