package com.imooc.bilibili.dao;


import com.imooc.bilibili.domain.Video;
import com.imooc.bilibili.domain.VideoTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoDao {

    Integer addVideos(Video video);

    Integer batchAddVideoTags(@Param("videoTagList") List<VideoTag> videoTagList);
}
