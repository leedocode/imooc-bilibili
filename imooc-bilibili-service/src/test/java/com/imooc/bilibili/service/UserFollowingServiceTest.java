package com.imooc.bilibili.service;

import com.imooc.bilibili.dao.FollowingGroupDao;
import com.imooc.bilibili.dao.UserDao;
import com.imooc.bilibili.domain.FollowingGroup;
import com.imooc.bilibili.domain.UserFollowing;
import com.imooc.bilibili.service.UserFollowingService;
import com.imooc.bilibili.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;



@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserFollowingServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserFollowingService userFollowingService;

    @Autowired
    FollowingGroupDao followingGroupDao;

    @Autowired
    UserDao userDao;

    @Test
    public void addUserFollowing() {
        UserFollowing userFollowing = new UserFollowing();
        userFollowing.setUserId(17L);
        userFollowing.setFollowingId(18L);
        userFollowing.setUserInfo(userService.getUserInfoByUserId(17L));
        userFollowingService.addUserFollowing(userFollowing);
    }

    @Test
    public void getUserFollowingsTest() {
        List<FollowingGroup> userFollowings = userFollowingService.getUserFollowings(17L);
        for (FollowingGroup followingGroup : userFollowings) {
            System.out.println(followingGroup);
            System.out.println("---");
        }
    }

    @Test
    public void getUserFans() {
        List<UserFollowing> userFans = userFollowingService.getUserFans(17L);
        for (UserFollowing userFollowing : userFans) {
            System.out.println(userFollowing);
            System.out.println("---");
        }
    }

    @Test
    public void testDao() {
        FollowingGroup followingGroup = followingGroupDao.getByType("2");
        System.out.println(followingGroup);
    }


    @Test
    public void addUserFollowingGroup() {
        FollowingGroup followingGroup = new FollowingGroup();
        followingGroup.setUserId(17L);
        followingGroup.setName("吃播");
        Long id = userFollowingService.addUserFollowingGroup(followingGroup);
        log.info("id = {}", id);
    }

    @Test
    public void getUserFollowingGroups() {
        List<FollowingGroup> userFollowingGroups = userFollowingService.getUserFollowingGroups(17L);
        for (FollowingGroup group : userFollowingGroups) {
            log.info("group = {}", group);
        }
    }

    @Test
    public void testLike() {
        Map<String, Object> map = new HashMap<>();
        map.put("nick", "小");
        Integer integer = userDao.pageCountUserInfos(map);
        System.out.println(integer);
    }
}