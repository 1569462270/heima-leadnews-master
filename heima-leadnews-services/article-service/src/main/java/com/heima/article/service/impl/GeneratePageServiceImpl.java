package com.heima.article.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.mapper.ApAuthorMapper;
import com.heima.article.service.GeneratePageService;
import com.heima.common.exception.CustException;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.entity.ApArticle;
import com.heima.model.article.entity.ApAuthor;
import com.heima.model.common.enums.AppHttpCodeEnum;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author : MR.wu
 * @Description : freemarker生成页面service实现类
 * @Date : 2022/12/29 20:17
 * @Version : 1.0
 */
@Slf4j
@Service
public class GeneratePageServiceImpl implements GeneratePageService {

    @Autowired
    private Configuration configuration;
    @Resource(name = "minIOFileStorageService")
    private FileStorageService fileStorageService;
    @Value("${file.minio.prefix}")
    private String prefix;
    @Autowired
    private ApArticleMapper apArticleMapper;
    @Autowired
    private ApAuthorMapper authorMapper;

    /**
     * 生成文章静态页
     */
    @Override
    public void generateArticlePage(String content, ApArticle apArticle) {
        // 获取文章内容
        try {
            // 模板
            Template template = configuration.getTemplate("article.ftl");

            // 数据
            Map<String, Object> params = new HashMap<>();
            // 文章详情
            params.put("content", JSONArray.parseArray(content));
            // 文章信息
            params.put("article", apArticle);
            ApAuthor author = authorMapper.selectById(apArticle.getAuthorId());
            // 作者对应的apUserId
            params.put("authorApUserId", author.getUserId());
            StringWriter out = new StringWriter();
            template.process(params, out);
            InputStream is = new ByteArrayInputStream(out.toString().getBytes());
            // 生成页面把html文件上传到minio中
            String path = fileStorageService.store(prefix, apArticle.getId() + ".html", "text/html", is);

            // 修改ap_article表，保存static_url字段
            apArticle.setStaticUrl(path);
            apArticleMapper.updateById(apArticle);
            log.info("文章详情静态页生成成功 staticUrl=====> {}", path);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("文章详情静态页生成失败=====>articleId : {}    ========> {}", apArticle.getId(), e.getCause());
            CustException.cust(AppHttpCodeEnum.SERVER_ERROR, "文章详情静态页生成失败");
        }
    }
}
