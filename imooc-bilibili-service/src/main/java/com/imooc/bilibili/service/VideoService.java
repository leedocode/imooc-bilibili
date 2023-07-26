package com.imooc.bilibili.service;

import com.imooc.bilibili.dao.VideoDao;
import com.imooc.bilibili.domain.*;
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

    public void addVideoLike(Long videoId, Long userId) {
        Video video = videoDao.getVideoId(videoId);
        if (video == null) {
            throw new ConditionException("非法视频");
        }
        VideoLike videoLike = videoDao.getVideoLikeByUserIdAndVideoId(videoId, userId);
        if (videoLike != null) {
            throw new ConditionException("视频已经点过赞!");
        }
        videoLike = new VideoLike();
        videoLike.setVideoId(videoId);
        videoLike.setUserId(userId);
        videoLike.setCreateTime(new Date());
        videoDao.addVideoLike(videoLike);
    }

    public void deleteVideoLike(Long videoId, Long userId) {
        videoDao.deleteVideoLike(videoId, userId);
    }

    public Map<String, Object> getVideoLikes(Long videoId, Long userId) {
        Long count = videoDao.getVideoLikes(videoId, userId);
        VideoLike videoLike = videoDao.getVideoLikeByUserIdAndVideoId(videoId, userId);
        boolean like = videoLike != null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("like", like);
        return result;
    }

    @Transactional
    public void addVideoCollection(VideoCollection videoCollection, Long userId) {
        Long videoId = videoCollection.getVideoId();
        Long groupId = videoCollection.getGroupId();
        if (videoId == null || groupId == null) {
            throw new ConditionException("参数异常");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        //先删除原有的收藏视频
        deleteVideoCollection(videoId, userId);
        videoCollection.setUserId(userId);
        videoCollection.setCreateTime(new Date());
        videoDao.addVideoCollection(videoCollection);
    }

    public void deleteVideoCollection(Long videoId, Long userId) {
        videoDao.deleteVideoCollection(videoId, userId);
    }

    public Map<String, Object> getVideoCollections(Long videoId, Long userId) {
        Long count = videoDao.getVideoCollections(videoId, userId);
        VideoCollection videoCollection = videoDao.getVideoCollectionByVideoIdAndUserId(videoId, userId);
        boolean isCollected = videoCollection != null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("isCollected", isCollected);
        return result;
    }
}
