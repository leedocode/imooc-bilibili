package com.imooc.bilibili.service;

import com.imooc.bilibili.dao.DemoDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * 描述: TODO
 */
@Service
public class DemoService {

    @Autowired
    DemoDao demoDao;

    public Map<String, Object> query(Long id) {
        return demoDao.query(id);
    }
}
