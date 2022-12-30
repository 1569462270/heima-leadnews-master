package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dto.ArticleHomeDTO;
import com.heima.model.article.entity.ApArticle;
import com.heima.model.common.dto.ResponseResult;

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


    /**
     * 加载文章列表
     *
     * @param loadType 0为加载更多  1为加载最新
     * @param dto      dto
     * @return {@code ResponseResult}
     */
    ResponseResult load(Short loadType, ArticleHomeDTO dto);
}
