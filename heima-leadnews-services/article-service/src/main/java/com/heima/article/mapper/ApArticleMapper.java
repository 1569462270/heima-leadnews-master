package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dto.ArticleHomeDTO;
import com.heima.model.article.entity.ApArticle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author : MR.wu
 * @Description : App已发布文章Mapper接口
 * @Date : 2022/12/28 13:08
 * @Version : 1.0
 */
public interface ApArticleMapper extends BaseMapper<ApArticle> {

    /**
     * 加载文章列表
     *
     * @param dto  dto
     * @param type 类型 0：加载更多   1：加载最新
     * @return {@code List<ApArticle>}
     */
    List<ApArticle> loadArticleList(@Param("dto") ArticleHomeDTO dto,
                                    @Param("type") Short type);
}
