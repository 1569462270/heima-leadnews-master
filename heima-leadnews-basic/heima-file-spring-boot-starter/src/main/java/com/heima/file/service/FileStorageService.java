package com.heima.file.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author : MR.wu
 * @Description : 文件上传接口
 * @Date : 2022/12/24 13:11
 * @Version : 1.0
 */
public interface FileStorageService {

    /**
     * @param prefix      文件上传前缀
     * @param filename    文件名称
     * @param inputStream 文件流
     * @return pathUrl 全路径
     * @Description 文件上传
     */
    String store(String prefix, String filename, InputStream inputStream);


    /**
     * @param prefix      文件上传前缀
     * @param filename    文件名称
     * @param contentType 文件类型 "image/jpg" 或"text/html"
     * @param inputStream 文件流
     * @return pathUrl 全路径
     * @Description 文件上传
     */
    String store(String prefix, String filename, String contentType, InputStream inputStream);

    /**
     * @param pathUrl 全路径
     * @Description 文件删除
     */
    void delete(String pathUrl);


    /**
     * @param pathUrls 全路径集合
     * @Description 批量文件删除
     */
    void deleteBatch(List<String> pathUrls);

    /**
     * @param pathUrl 全路径
     * @Description 下载文件
     */
    InputStream downloadFile(String pathUrl);

    /**
     * @param pathUrl 全路径
     * @Description 获取文件文本内容
     */
    String getFileContent(String pathUrl) throws IOException;
}
