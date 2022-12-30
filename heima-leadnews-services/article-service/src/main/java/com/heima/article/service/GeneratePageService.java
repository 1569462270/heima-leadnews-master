package com.heima.article.service;

import com.heima.model.article.entity.ApArticle;

/**
 * @Author : MR.wu
 * @Description : freemarker生成页面service
 * @Date : 2022/12/29 20:16
 * @Version : 1.0
 */
public interface GeneratePageService {

    /**
     * 生成文章静态页
     */
    void generateArticlePage(String content, ApArticle apArticle);
}
