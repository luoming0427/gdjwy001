package com.hirisun.cloud.file.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhoufeng
 * @version 1.0
 * @className FileService
 * @data 2020/7/2 11:30
 * @description 文件服务接口
 */
public interface FileService {

    /**
     * Fdfs上传文件
     * @param file
     * @return
     */
    String fdfs_upload(MultipartFile file);
    
    /**
     * 根据fileId删除文件
     * @param fileId
     * @return
     */
    Integer deleteFileByFileId(String fileId);
    
    /**
     * 根据fileId下载文件
     * @param fileId
     * @return
     */
    byte[] downloadFileByFileId(String fileId);
}