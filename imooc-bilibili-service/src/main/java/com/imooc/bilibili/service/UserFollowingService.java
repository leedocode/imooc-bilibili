package com.imooc.bilibili.service;

import com.imooc.bilibili.dao.UserFollowingDao;
import com.imooc.bilibili.domain.FollowingGroup;
import com.imooc.bilibili.domain.User;
import com.imooc.bilibili.domain.UserFollowing;
import com.imooc.bilibili.domain.UserInfo;
import com.imooc.bilibili.domain.constant.UserConstant;
import com.imooc.bilibili.service.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 描述: TODO
 */
@Service
public class UserFollowingService {

    @Autowired
    UserFollowingDao userFollowingDao;

    @Autowired
    FollowingGroupService followingGroupService;

    @Autowired
    UserService userService;


    @Transactional
    public void addUserFollowing(UserFollowing userFollowing) {
        Long groupId = userFollowing.getGroupId();
        if (groupId == null) {
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE);
            userFollowing.setGroupId(followingGroup.getId());
        } else {
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if (followingGroup == null) {
                throw new ConditionException("关注分组不存在!");
            }
        }
        Long followingId = userFollowing.getFollowingId();
        User user = userService.getUserById(followingId);
        if (user == null) {
            throw new ConditionException("关注的用户不存在!");
        }
        //先删除再加入用户关注信息，可以保证每次加入都是最新的关注人
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(), followingId);
        userFollowing.setCreateTime(new Date());
        userFollowingDao.addUserFollowing(userFollowing);
//        UserInfo followingUserInfo = userService.getUserInfoByUserId(followingId);
//        followingUserInfo.setFollowed(true);
//        userFollowing.setUserInfo(followingUserInfo);
    }

    //1 获取关注的用户列表
    //2 根据关注用户的id查询关注用户的基本信息
    //3 被关注用户按关注分组进行分类
    public List<FollowingGroup> getUserFollowings(Long userId) {
        //获取当前用户关注的所有其他用户ID
        List<UserFollowing> userFollowingList = userFollowingDao.getUserFollowings(userId);
        Set<Long> followingIdSet = userFollowingList.stream()
                .map(UserFollowing::getFollowingId)
                .collect(Collectors.toSet());
        //获取关注的其他所有用户的用户信息userInfo
        List<UserInfo> userInfoList = new ArrayList<>();
        if (followingIdSet.size() > 0) {
            userInfoList = userService.getUserInfosByUserIdS(followingIdSet);
        }
//      测试如果把关注分组里的followed字段改了会是什么结果，前端这里是写死的，不管是什么值，在关注分组里都是已关注状态，如果未关注的话就不会出现在分组中
        //其实也能理解，不是你关注的人，自然不会出现在你的关注分组中，所以这里也无所谓要不要加followed=true这个操作
//        for (UserInfo u : userInfoList) {
//            u.setFollowed(false);
//        }
        //设置userInfo到对应的用户关注字段中
        for (UserFollowing userFollowing : userFollowingList) {
            for (UserInfo userInfo : userInfoList) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
        //关注分组信息需要定义当前用户id，也就是每个当前用户id有他的关注分组信息
        //比如用户A，有三个分组 1分组 2分组 3分组、
        //getByUserId方法会返回对应userId的分组和我们所创建的默认分组,保证了用户没有新建分组的情况下，可以把自己的关注用户添加到默认分组中
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);


        //为每个分组设置对应的分组中包含的userInfo信息，分组中包含多个用户信息
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);
        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);

        //不同的分组放置对应分组id的用户数据到分组的userInfoList中
        for (FollowingGroup group : groupList) {
            List<UserInfo> infoList = new ArrayList<>();
            for (UserFollowing userFollowing : userFollowingList) {
                if (group.getId().equals(userFollowing.getGroupId())) {
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            group.setFollowingUserInfoList(infoList);
            result.add(group);
        }
        return result;
    }


    //1 获取用户的粉丝列表
    //2 设置粉丝的用户信息
    //3 查询是否和当前用户属于互粉状态，是的话就设置标志位
    public List<UserFollowing> getUserFans(Long userId) {
        List<UserFollowing> fanList = userFollowingDao.getUserFans(userId);
        Set<Long> fanIdSet = fanList.stream()
                .map(UserFollowing::getUserId)
                .collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        if (fanIdSet.size() > 0) {
            userInfoList = userService.getUserInfosByUserIdS(fanIdSet);
        }
        List<UserFollowing> userFollowings = userFollowingDao.getUserFollowings(userId);

        for (UserFollowing fan : fanList) {
            for (UserInfo userInfo : userInfoList) {
                if (fan.getUserId().equals(userInfo.getUserId())) {
                    userInfo.setFollowed(false);
                    fan.setUserInfo(userInfo);
                }
            }

            for (UserFollowing userFollowing : userFollowings) {
                if (userFollowing.getFollowingId().equals(fan.getUserId())) {
                    fan.getUserInfo().setFollowed(true);
                }
            }
        }
        return fanList;
    }

    public Long addUserFollowingGroup(FollowingGroup followingGroup) {
        followingGroup.setCreateTime(new Date());
        followingGroup.setType(UserConstant.USER_FOLLOWING_GROUP_TYPE_USER_CUSTOMIZE);
        followingGroupService.addFollowingGroup(followingGroup);
        return followingGroup.getId();
    }

    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupService.getUserFollowingGroups(userId);
    }

    public List<UserInfo> checkFollowingStatus(List<UserInfo> list, Long userId) {
        List<UserFollowing> userFollowings = userFollowingDao.getUserFollowings(userId);
        for (UserInfo userInfo : list) {
            userInfo.setFollowed(false);
            for (UserFollowing userFollowing : userFollowings) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userInfo.setFollowed(true);
                }
            }
        }
        return list;
    }
}
