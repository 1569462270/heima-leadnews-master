package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.ApArticle;

/**
 * @Author : MR.wu
 * @Description : App已发布文章service接口
 * @Date : 2022/12/28 13:10
 * @Version : 1.0
 */
public interface ApArticleService extends IService<ApArticle> {

    /**
     * 发表文章
     *
     * @param newsId 文章id
     */
    void publishArticle(Integer newsId);
}
