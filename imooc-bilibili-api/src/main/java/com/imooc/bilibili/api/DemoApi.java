package com.imooc.bilibili.api;

import com.imooc.bilibili.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 描述: TODO
 */
@RestController
public class DemoApi {

    @Autowired
    DemoService demoService;


    @GetMapping("/query")
    private Map<String, Object> query(Long id) {
        return demoService.query(id);
    }
}
