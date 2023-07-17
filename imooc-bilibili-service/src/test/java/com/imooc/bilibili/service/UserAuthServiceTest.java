package com.imooc.bilibili.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


// 测试时需要加上对应的配置项到配置文件中
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserAuthServiceTest {


    @Autowired
    UserAuthService userAuthService;

    @Test
    public void addUserDefaultRole() {
        Long userId = 19L;
        userAuthService.addUserDefaultRole(userId);
    }
}