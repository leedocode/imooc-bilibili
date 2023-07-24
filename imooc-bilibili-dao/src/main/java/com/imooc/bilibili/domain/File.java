package com.imooc.bilibili.domain;

import lombok.Data;

import java.util.Date;

/**
 * 描述: 定义了上传成功的文件相关信息
 */

@Data
public class File {

    private Long id;

    private String url;

    private String type;

    private String md5;

    private Date createTime;

}
