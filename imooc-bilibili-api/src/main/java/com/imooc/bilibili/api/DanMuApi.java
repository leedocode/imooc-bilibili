package com.imooc.bilibili.api;

import com.imooc.bilibili.api.support.UserSupport;
import com.imooc.bilibili.domain.DanMu;
import com.imooc.bilibili.domain.JsonResponse;
import com.imooc.bilibili.service.DanMuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述: TODO
 */
@RestController
public class DanMuApi {


    @Autowired
    private DanMuService danmuService;

    @Autowired
    private UserSupport userSupport;

    @GetMapping("/danmus")
    public JsonResponse<List<DanMu>> getDanmus(@RequestParam Long videoId,
                                               String startTime,
                                               String endTime) throws Exception {
        List<DanMu> list;
        Map<String, Object> map = new HashMap<>();
        map.put("videoId", videoId);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        try{
            //判断当前是游客模式还是用户登录模式
            userSupport.getCurrentUserId();
            //若是用户登录模式，则允许用户进行时间段筛选
            list = danmuService.getDanMus(map);
        }catch (Exception ignored){
            //若为游客模式，则不允许用户进行时间段筛选
            map.put("startTime", null);
            map.put("endTime", null);
            list = danmuService.getDanMus(map);
        }
        return new JsonResponse<>(list);
    }
}
