package com.imooc.bilibili.service.util;

import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.imooc.bilibili.service.exception.ConditionException;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 描述: FastDFS 工具类
 */

@Component
public class FastDFSUtil {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String DEFAULT_GROUP = "group1";

    public static final String PATH_KEY = "path-key:";

    public static final String UPLOAD_SIZE_KEY = "upload-size-key:";

    public static final String UPLOAD_NO_KEY = "upload-no-key";

    public static final int SLICE_SIZE = 1024 * 1024 * 2;

    public String getFileType(MultipartFile file) {
        if (file == null) {
            throw new ConditionException("非法文件!");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new ConditionException("文件名错误！");
        }
        if (!originalFilename.contains(".")) {
            throw new ConditionException("文件没有类型后缀");
        }
        int index = originalFilename.lastIndexOf(".");
        return originalFilename.substring(index + 1);
    }

    //上传文件
    public String uploadCommonFile(MultipartFile file) throws IOException {
        Set<MetaData> metaDataSet = new HashSet<>();
        String fileType = getFileType(file);
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileType, metaDataSet);
        return storePath.getPath();
    }

    //上传可以断点续传的文件
    public String uploadAppenderFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String fileType = getFileType(file);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }

    //继续添加文件 可重传的方式
    public void modifyAppenderFile(MultipartFile file, String filePath, long fileOffset) throws IOException {
        appendFileStorageClient.modifyFile(DEFAULT_GROUP, filePath, file.getInputStream(), file.getSize(), fileOffset);
    }



    public String uploadFileBySlices(MultipartFile file, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws Exception{
        if (file == null || fileMd5 == null || totalSliceNo == null) {
            throw new ConditionException("参数异常！");
        }

        String pathKey = PATH_KEY + fileMd5;
        String uploadSizeKey = UPLOAD_SIZE_KEY + fileMd5; // 已经上传文件大小的key
        String uploadNoKey = UPLOAD_NO_KEY + fileMd5; // 已经上传分片个数的key

        //判断一下是否是第一个分片
        String uploadSizeStr = redisTemplate.opsForValue().get(uploadSizeKey);
        Long uploadSize = 0L;
        if (!StringUtil.isNullOrEmpty(uploadSizeStr)) {
            uploadSize = Long.valueOf(uploadSizeStr);
        }

        if (sliceNo == 1) {
            String path = uploadAppenderFile(file);
            if (StringUtil.isNullOrEmpty(path)) {
                throw new ConditionException("文件上传失败");
            }
            redisTemplate.opsForValue().set(pathKey, path);
            redisTemplate.opsForValue().set(uploadNoKey, "1");
        } else {
            String filePath = redisTemplate.opsForValue().get(pathKey);
            if (StringUtil.isNullOrEmpty(filePath)) {
                throw new ConditionException("文件上传失败");
            }
            modifyAppenderFile(file, filePath, uploadSize);
            redisTemplate.opsForValue().increment(uploadNoKey);
        }
        //修改历史上传文件大小数值
        uploadSize += file.getSize();
        redisTemplate.opsForValue().set(uploadSizeKey, String.valueOf(uploadSize));
        //判断是否已经上传完最后一个分片
        String uploadNoStr = redisTemplate.opsForValue().get(uploadNoKey);
        if (StringUtil.isNullOrEmpty(uploadNoStr)) {
            throw new ConditionException("获取分片数量异常");
        }
        Integer uploadNo = Integer.valueOf(uploadNoStr);
        String resultPath = "";
        if (uploadNo.equals(totalSliceNo)) {
            resultPath = redisTemplate.opsForValue().get(pathKey);
            List<String> keyList = Arrays.asList(pathKey, uploadSizeKey, uploadNoKey);
            redisTemplate.delete(keyList);
        }
        return resultPath;
    }

    public void convertFileToSlices(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileType = getFileType(multipartFile);
        File file = multipartFile2File(multipartFile);
        int count = 1;
        long fileLen = file.length();
        for (long i = 0; i < file.length(); i += SLICE_SIZE) {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(i);
            byte[] bytes = new byte[SLICE_SIZE];
            int len = randomAccessFile.read(bytes);
            String path = "tmpfile/" + count + "." + fileType;
            File slice = new File(path);
            FileOutputStream fos = new FileOutputStream(slice);
            fos.write(bytes, 0, len);
            fos.close();
            randomAccessFile.close();
            count++;
        }
        file.delete();
    }

    public File multipartFile2File(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String[] fileName = originalFilename.split("\\.");
        File file = File.createTempFile(fileName[0], "." + fileName[1]);
        multipartFile.transferTo(file);
        return file;
    }


    public void deleteFile(String filePath) {
        fastFileStorageClient.deleteFile(filePath);
    }

//    public static void main(String[] args) throws IOException {
//        File file = new File("tmpfile/1.txt");
//        FileOutputStream fos = new FileOutputStream(file);
//        byte[] bytes = new byte[20];
//        for (int i = 0; i < bytes.length; i++) {
//            bytes[i] = 'c';
//        }
//
//        fos.write(bytes, 0 , 20);
//        System.out.println(file.getAbsolutePath());
//    }

}
