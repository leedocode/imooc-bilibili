package com.imooc.bilibili.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述: TODO
 */

@Data
public class Video {

    private Long id;

    private Long userId;//用户id

    private String url; //视频链接

    private String thumbnail;//封面

    private String title; //标题

    private String type;// 0自制 1转载

    private String duration;//时长

    private String area;//分区

    private List<VideoTag> videoTagList = new ArrayList<>();//标签列表

    private String description;//简介

    private Date createTime;

    private Date updateTime;
}
