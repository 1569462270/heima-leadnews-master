package com.heima.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.service.ApArticleContentService;
import com.heima.model.article.ApArticleContent;
import org.springframework.stereotype.Service;

/**
 * @Author : MR.wu
 * @Description : App 已发布文章内容service实现类
 * @Date : 2022/12/28 13:15
 * @Version : 1.0
 */
@Service
public class ApArticleContentServiceImpl extends ServiceImpl<ApArticleContentMapper, ApArticleContent> implements ApArticleContentService {
}
