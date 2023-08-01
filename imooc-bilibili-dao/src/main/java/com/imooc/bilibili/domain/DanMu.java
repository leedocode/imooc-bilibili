package com.imooc.bilibili.domain;

import lombok.Data;

import java.util.Date;

/**
 * 描述: TODO
 */

@Data
public class DanMu {
    private Long id;

    private Long userId;

    private Long videoId;

    private String content;

    private String danmuTime;

    private Date createTime;
}
