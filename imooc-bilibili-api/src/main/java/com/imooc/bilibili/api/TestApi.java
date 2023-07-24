package com.imooc.bilibili.api;

import com.imooc.bilibili.service.util.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 描述: 对文件进行分片
 */

@RestController
public class TestApi {
    @Autowired
    FastDFSUtil fastDFSUtil;

    @GetMapping("/slices")
    public void slices(MultipartFile file) throws IOException {
        fastDFSUtil.convertFileToSlices(file);
    }
}
