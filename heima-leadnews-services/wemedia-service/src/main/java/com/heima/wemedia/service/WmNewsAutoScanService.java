package com.heima.wemedia.service;

/**
 * @Author : MR.wu
 * @Description : 自媒体文章自动审核service接口
 * @Date : 2022/12/26 17:59
 * @Version : 1.0
 */
public interface WmNewsAutoScanService {

    /**
     * 自动审核文章
     *
     * @param id 文章id
     */
    void autoScanWmNews(Integer id);
}
