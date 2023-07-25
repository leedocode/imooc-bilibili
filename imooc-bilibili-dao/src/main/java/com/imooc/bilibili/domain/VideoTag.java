package com.imooc.bilibili.domain;

import lombok.Data;

import java.util.Date;

/**
 * 描述: TODO
 */
@Data
public class VideoTag {

    private Long id;

    private Long videoId;

    private Long tagId;

    private Date createTime;

}
