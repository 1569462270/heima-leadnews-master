package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.wemedia.dto.NewsAuthDTO;
import com.heima.model.wemedia.dto.WmNewsDTO;
import com.heima.model.wemedia.dto.WmNewsPageReqDTO;
import com.heima.model.wemedia.entity.WmNews;

/**
 * @Author : MR.wu
 * @Description : 自媒体图文内容service接口
 * @Date : 2022/12/25 12:02
 * @Version : 1.0
 */
public interface WmNewsService extends IService<WmNews> {

    /**
     * 文章列表查询
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    ResponseResult findList(WmNewsPageReqDTO dto);


    /**
     * 自媒体文章发布
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    ResponseResult submitNews(WmNewsDTO dto);

    /**
     * 根据文章id查询文章
     *
     * @param id id
     * @return {@code ResponseResult}
     */
    ResponseResult findWmNewsById(Integer id);

    /**
     * 删除文章
     *
     * @param id id
     * @return {@code ResponseResult}
     */
    ResponseResult delNews(Integer id);


    /**
     * 上下架
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    ResponseResult downOrUp(WmNewsDTO dto);

    /**
     * 查询文章列表
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    ResponseResult findList(NewsAuthDTO dto);

    /**
     * 文章详情
     *
     * @param id id
     * @return {@code ResponseResult}
     */
    ResponseResult findWmNewsVo(Integer id);

    /**
     * 人工审核
     *
     * @param status 状态
     * @param dto    dto
     * @return {@code ResponseResult}
     */
    ResponseResult updateStatus(Short status, NewsAuthDTO dto);
}
