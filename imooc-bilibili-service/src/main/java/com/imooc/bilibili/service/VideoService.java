package com.imooc.bilibili.service;

import com.imooc.bilibili.dao.VideoDao;
import com.imooc.bilibili.domain.PageResult;
import com.imooc.bilibili.domain.Video;
import com.imooc.bilibili.domain.VideoTag;
import com.imooc.bilibili.service.exception.ConditionException;
import com.imooc.bilibili.service.util.FastDFSUtil;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 描述: TODO
 */
@Service
public class VideoService {

    @Autowired
    VideoDao videoDao;

    @Autowired
    FastDFSUtil fastDFSUtil;

    @Transactional
    public void addVideos(Video video) {
        Date now = new Date();
        video.setCreateTime(now);
        videoDao.addVideos(video);
        Long videoId = video.getId();
        List<VideoTag> videoTagList = video.getVideoTagList();
        if (!videoTagList.isEmpty()) { // 不加判断 在insert集合为空时，sql语句会报错
            videoTagList.forEach(item -> {
                item.setVideoId(videoId);
                item.setCreateTime(now);
            });
            videoDao.batchAddVideoTags(videoTagList);
        }
    }

    public PageResult<Video> pageListVideos(Integer size, Integer no, String area) {
        if (size == null || no == null || StringUtil.isNullOrEmpty(area)) {
            throw new ConditionException("参数异常");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        params.put("area", area);
        Integer total = videoDao.pageCountVideos(params);
        List<Video> videoList = new ArrayList<>();
        if (total > 0) {
            videoList = videoDao.pageListVideos(params);
        }

        return new PageResult<Video>(total, videoList);
    }

    public void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String url) throws Exception {
        fastDFSUtil.viewVideoOnlineBySlices(request, response, url);
    }
}
