package com.imooc.bilibili.service;

import com.imooc.bilibili.dao.VideoDao;
import com.imooc.bilibili.domain.Video;
import com.imooc.bilibili.domain.VideoTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 描述: TODO
 */
@Service
public class VideoService {

    @Autowired
    VideoDao videoDao;

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
}
