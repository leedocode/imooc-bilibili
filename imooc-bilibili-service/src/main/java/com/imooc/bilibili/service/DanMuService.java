package com.imooc.bilibili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.dao.DanMuDao;
import com.imooc.bilibili.domain.DanMu;
import com.mysql.cj.xdevapi.JsonArray;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 描述: TODO
 */

@Service
public class DanMuService {

    @Autowired
    private DanMuDao danMuDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void addDanMu(DanMu danMu) {
        danMuDao.addDanMu(danMu);
    }

    public List<DanMu> getDanMus(Map<String, Object> params) {
        return danMuDao.getDanMus(params);
    }

    //弹幕持久化到redis 高性能
    public void addDanMusToRedis(DanMu danMu) {
        String key = "danmu-video-" + danMu.getVideoId();
        String value = redisTemplate.opsForValue().get(key);
        List<DanMu> list = new ArrayList<>();
        if (!StringUtil.isNullOrEmpty(value)) {
            list = JSONArray.parseArray(value, DanMu.class);
        }
        list.add(danMu);
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list));
    }
}
