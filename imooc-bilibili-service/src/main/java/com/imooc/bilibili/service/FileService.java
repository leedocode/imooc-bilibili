package com.imooc.bilibili.service;

import com.imooc.bilibili.dao.FileDao;
import com.imooc.bilibili.domain.File;
import com.imooc.bilibili.service.util.FastDFSUtil;
import com.imooc.bilibili.service.util.MD5Util;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

/**
 * 描述: TODO
 */

@Service
public class FileService {

    @Autowired
    FileDao fileDao;

    @Autowired
    FastDFSUtil fastDFSUtil;

    public String uploadFileBySlices(MultipartFile file, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws Exception {
        File doFileMD5 = fileDao.getFileByMD5(fileMd5);
        if (doFileMD5 != null) {
            return doFileMD5.getUrl();
        }
        String url = fastDFSUtil.uploadFileBySlices(file, fileMd5, sliceNo, totalSliceNo);
        if (!StringUtil.isNullOrEmpty(url)) {
            doFileMD5 = new File();
            doFileMD5.setType(fastDFSUtil.getFileType(file));
            doFileMD5.setUrl(url);
            doFileMD5.setMd5(fileMd5);
            doFileMD5.setCreateTime(new Date());
            fileDao.addFile(doFileMD5);
        }
        return url;
    }

    public String getFileMD5(MultipartFile file) throws IOException {
        String fileMD5 = MD5Util.getFileMD5(file);
        return fileMD5;
    }
}
