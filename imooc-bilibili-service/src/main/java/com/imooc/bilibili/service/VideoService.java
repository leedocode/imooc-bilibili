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
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 描述: TODO
 */
@Service
public class VideoService {

    @Autowired
    VideoDao videoDao;

    @Autowired
    FastDFSUtil fastDFSUtil;

    @Autowired
    UserCoinService userCoinService;

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

    @Transactional
    public void addVideoCoins(VideoCoin videoCoin, Long userId) {
        Long videoId = videoCoin.getVideoId();
        Integer amount = videoCoin.getAmount();
        if (videoId == null) {
            throw new ConditionException("非法视频ID");
        }
        if (amount == null) {
            throw new ConditionException("非法投币数量");
        }
        Integer userCoinAmount = userCoinService.getUserCoinAmount(userId);
        userCoinAmount = userCoinAmount == null ? 0 : userCoinAmount;
        if (userCoinAmount < amount) {
            throw new ConditionException("用户硬币数量不足");
        }
        VideoCoin dbVideoCoin = videoDao.getVideoCoinByVideoIdAndUserId(videoId, userId);
        if (dbVideoCoin == null) {
            dbVideoCoin = new VideoCoin();
            dbVideoCoin.setUserId(userId);
            dbVideoCoin.setVideoId(videoId);
            dbVideoCoin.setAmount(amount);
            dbVideoCoin.setCreateTime(new Date());
            videoDao.addVideoCoin(dbVideoCoin);
        } else {
            dbVideoCoin.setAmount(dbVideoCoin.getAmount() + userCoinAmount);
            dbVideoCoin.setUserId(userId);
            dbVideoCoin.setUpdateTime(new Date());
            videoDao.updateVideoCoin(dbVideoCoin);
        }
        userCoinService.updateVideoCoin(userId, (userCoinAmount - amount));
    }

    public Map<String, Object> getVideoCoins(Long videoId, Long userId) {
        Long count = videoDao.getVideoCoins(videoId);
        VideoCoin videoCoin = videoDao.getVideoCoinByVideoIdAndUserId(videoId, userId);
        boolean isCoined = videoCoin != null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("isCoined", isCoined);
        return result;
    }

    public void addVideoComment(VideoComment videoComment, Long userId) {
        Long videoId = videoComment.getVideoId();
        if (videoId == null) {
            throw new ConditionException("参数异常");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频");
        }
        videoComment.setUserId(userId);
        videoComment.setCreateTime(new Date());
        videoDao.addVideoComment(videoComment);
    }

    public PageResult<VideoComment> pageListVideoComments(Long size, Long no, Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        params.put("videoId", videoId);
        Integer total = videoDao.pageCountVideoComments(params);
        List<VideoComment> list = new ArrayList<>();
        if (total > 0) {
            //查询一级评论
            list = videoDao.pageListVideoComments(params);
            //查询二级评论
            List<Long> parentIdList = list.stream().map(VideoComment::getId).collect(Collectors.toList());
            List<VideoComment> childCommentList = videoDao.batchGetVideoCommentsByRootIds(parentIdList);
            //批量查询用户信息
            Set<Long> userIdList = list.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            Set<Long> replyUserIdList = childCommentList.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            userIdList.addAll(replyUserIdList);
            List<UserInfo> userInfoList = videoDao.batchGetUserInfoByUserIds(userIdList);
            Map<Long, UserInfo> userInfoMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));
            //遍历一级评论和二级评论设置对应的信息
            list.forEach(comment -> {
                Long id = comment.getId();
                List<VideoComment> childList = new ArrayList<>();
                childCommentList.forEach(child-> {
                    if (id.equals(child.getRootId())) {
                        child.setUserInfo(userInfoMap.get(child.getUserId()));
                        child.setReplyUserInfo(userInfoMap.get(child.getReplyUserId()));
                        // TODO: 这里可以考虑用递归来遍历最底层的评论， 原写法限制只能两级评论
                        childList.add(child);
                    }
                });
                comment.setChildCommentList(childList);
                comment.setUserInfo(userInfoMap.get(comment.getUserId()));
            });
        }

        return new PageResult<>(total, list);
    }
}
