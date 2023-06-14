package com.imooc.bilibili.domain;

import lombok.Data;

import java.util.List;

/**
 * 描述: 返回分页结果
 */

@Data
public class PageResult<T> {

    private Integer total;

    private List<T> list;

    public PageResult(Integer total, List<T> list) {
        this.total = total;
        this.list = list;
    }
}
