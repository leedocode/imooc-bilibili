package com.imooc.bilibili.service;

import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.dao.UserDao;
import com.imooc.bilibili.domain.PageResult;
import com.imooc.bilibili.domain.RefreshTokenDetail;
import com.imooc.bilibili.domain.User;
import com.imooc.bilibili.domain.UserInfo;
import com.imooc.bilibili.domain.constant.UserConstant;
import com.imooc.bilibili.service.exception.ConditionException;
import com.imooc.bilibili.service.util.MD5Util;
import com.imooc.bilibili.service.util.RSAUtil;
import com.imooc.bilibili.service.util.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 描述: TODO
 */

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthService userAuthService;

    public void addUser(User user) {
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空！");
        }
        User dbUser = this.getUserByPhone(phone);
        if (dbUser != null) {
            throw new ConditionException("该手机号已经注册！");
        }

        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }

        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(now);
        userDao.addUser(user);
        //添加用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        if (user.getNickName() != null && !user.getNickName().equals("")) {
            userInfo.setNick(user.getNickName());
        } else {
            userInfo.setNick(UserConstant.DEFAULT_NICK);
        }
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_MALE);
        userInfo.setCreateTime(now);
        userDao.addUserInfo(userInfo);
        //添加用户默认角色
        userAuthService.addUserDefaultRole(user.getId());

    }

    public User getUserByPhone(String phone) {
        return userDao.getUserByPhone(phone);
    }

    public String login(User user) throws Exception{
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空！");
        }

        User dbUser = userDao.getUserByPhone(phone);
        if (dbUser == null) {
            throw new ConditionException("当前用户不存在");
        }
        String password = user.getPassword();
        String rawPassword;

        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密错误！");
        }
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        if (!md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误！");
        }

        return TokenUtil.generateToken(dbUser.getId());
    }

    public Integer updateUser(User user) throws Exception {
        Long id = user.getId();
        User dbUser = userDao.getUserById(id);
        if (dbUser == null) {
            throw new ConditionException("用户不存在");
        }
        if (!StringUtils.isNullOrEmpty(user.getPassword())) {
            String rawPassword = RSAUtil.decrypt(user.getPassword());
            String md5Password = MD5Util.sign(rawPassword, dbUser.getSalt(), "UTF-8");
            user.setPassword(md5Password);
        }
        user.setUpdateTime(new Date());
        Integer cnt = userDao.updateUserByPrimaryKeySelective(user);
        if (cnt < 0) {
            throw new ConditionException("更新user失败，服务端错误");
        }
        return cnt;
    }

    public void updateUserInfo(UserInfo userInfo) {
        userInfo.setUpdateTime(new Date());
        Integer cnt = userDao.updateUserInfo(userInfo);
        if (cnt == 0) {
            throw new ConditionException("更新userInfo失败，服务端错误");
        }
    }

    public User getUserById(Long followingId) {
        return userDao.getUserById(followingId);
    }


    public List<UserInfo> getUserInfosByUserIdS(Set<Long> followingIdSet) {
        return userDao.getUserInfoByUserIds(followingIdSet);
    }

    public UserInfo getUserInfoByUserId(Long userId) {
        return userDao.getUserInfoByUserId(userId);
    }

    public PageResult<UserInfo> pageListUserInfos(JSONObject params) {
        Integer no = (Integer) params.get("no");
        Integer size =  (Integer) params.get("size");
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        Integer total = userDao.pageCountUserInfos(params);
        List<UserInfo> list = new ArrayList<>();
        if (total > 0) {
            list = userDao.pageListUserInfos(params);
        }
        return new PageResult<>(total, list);
    }

    public Map<String, Object> loginForDts(User user) throws Exception {
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空！");
        }

        User dbUser = userDao.getUserByPhone(phone);
        if (dbUser == null) {
            throw new ConditionException("当前用户不存在");
        }
        String password = user.getPassword();
        String rawPassword;

        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密错误！");
        }
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        if (!md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误！");
        }
        Long userId = dbUser.getId();
        String accessToken = TokenUtil.generateToken(userId);
        String refreshToken = TokenUtil.generateRefreshToken(userId);
        //保存refreshToken到数据库
        userDao.deleteRefreshToken(refreshToken, userId);
        userDao.addRefreshToken(refreshToken, userId, new Date());
        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        return result;
    }

    public void logout(String refreshToken, Long userId) {
        userDao.deleteRefreshToken(refreshToken, userId);
    }

    public String refreshAccessToken(String refreshToken) throws Exception {
        RefreshTokenDetail refreshTokenDetail = userDao.getRefreshTokenDetail(refreshToken);
        if (refreshTokenDetail == null) {
            throw new ConditionException("555", "token过期！");
        }
        Long userId = refreshTokenDetail.getUserId();
        return TokenUtil.generateToken(userId);
    }
}
