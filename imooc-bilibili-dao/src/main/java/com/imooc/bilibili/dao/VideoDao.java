package com.imooc.bilibili.dao;


import com.imooc.bilibili.domain.Video;
import com.imooc.bilibili.domain.VideoCollection;
import com.imooc.bilibili.domain.VideoLike;
import com.imooc.bilibili.domain.VideoTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface VideoDao {

    Integer addVideos(Video video);

    Integer batchAddVideoTags(@Param("videoTagList") List<VideoTag> videoTagList);

    Integer pageCountVideos(Map<String, Object> params);

    List<Video> pageListVideos(Map<String, Object> params);

    Video getVideoId(Long id);

    VideoLike getVideoLikeByUserIdAndVideoId(@Param("videoId") Long videoId, @Param("userId") Long userId);

    Integer addVideoLike(VideoLike videoLike);

    void deleteVideoLike(@Param("videoId") Long videoId, @Param("userId") Long userId);

    Long getVideoLikes(@Param("videoId") Long videoId, @Param("userId") Long userId);

    Integer addVideoCollection(VideoCollection videoCollection);

    Integer deleteVideoCollection(@Param("videoId") Long videoId, @Param("userId") Long userId);

    Video getVideoById(Long id);

    Long getVideoCollections(@Param("videoId") Long videoId, @Param("userId") Long userId);

    VideoCollection getVideoCollectionByVideoIdAndUserId(@Param("videoId") Long videoId, @Param("userId") Long userId);
}
