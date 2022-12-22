package com.heima.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApAuthorMapper;
import com.heima.article.service.ApAuthorService;
import com.heima.model.article.ApAuthor;
import org.springframework.stereotype.Service;

/**
 * @Author : MR.wu
 * @Description : 文章作者service实现类
 * @Date : 2022/12/22 14:52
 * @Version : 1.0
 */
@Service
public class ApAuthorServiceImpl extends ServiceImpl<ApAuthorMapper, ApAuthor> implements ApAuthorService {
}
