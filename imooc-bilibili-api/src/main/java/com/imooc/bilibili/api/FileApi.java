package com.imooc.bilibili.api;

import com.imooc.bilibili.domain.JsonResponse;
import com.imooc.bilibili.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 描述: 断点上传文件
 */

@RestController
public class FileApi {

    @Autowired
    FileService fileService;

    @PostMapping("/md5files")
    public JsonResponse<String> getFileMD5(MultipartFile file) throws IOException {
        String fileMD5 = fileService.getFileMD5(file);
        return new JsonResponse<>(fileMD5);
    }


    @PutMapping("/file-slices")
    public String uploadFileBySlices(MultipartFile slice, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws Exception {
        return fileService.uploadFileBySlices(slice, fileMd5, sliceNo, totalSliceNo);
    }
}
