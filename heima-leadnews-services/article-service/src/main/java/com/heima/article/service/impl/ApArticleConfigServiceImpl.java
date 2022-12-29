package com.heima.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.service.ApArticleConfigService;
import com.heima.model.article.ApArticleConfig;
import org.springframework.stereotype.Service;

/**
 * @Author : MR.wu
 * @Description : App 已发布文章配置service实现类
 * @Date : 2022/12/28 13:13
 * @Version : 1.0
 */
@Service
public class ApArticleConfigServiceImpl extends ServiceImpl<ApArticleConfigMapper, ApArticleConfig> implements ApArticleConfigService {
}
