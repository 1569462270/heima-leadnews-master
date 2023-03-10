package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.mapper.ApAuthorMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.GeneratePageService;
import com.heima.common.constants.admin.ArticleConstants;
import com.heima.common.exception.CustException;
import com.heima.feign.AdminFeign;
import com.heima.feign.WemediaFeign;
import com.heima.model.admin.entity.AdChannel;
import com.heima.model.article.dto.ArticleHomeDTO;
import com.heima.model.article.entity.ApArticle;
import com.heima.model.article.entity.ApArticleConfig;
import com.heima.model.article.entity.ApArticleContent;
import com.heima.model.article.entity.ApAuthor;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.entity.WmNews;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author : MR.wu
 * @Description : App已发布文章service实现类
 * @Date : 2022/12/28 13:11
 * @Version : 1.0
 */
@Service
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {
    @Autowired
    private AdminFeign adminFeign;

    @Autowired
    private WemediaFeign wemediaFeign;

    @Autowired
    private ApAuthorMapper authorMapper;

    @Autowired
    private ApArticleContentMapper articleContentMapper;

    @Autowired
    private ApArticleConfigMapper articleConfigMapper;

    @Autowired
    private ApArticleMapper apArticleMapper;
    @Value("${file.oss.web-site}")
    private String webSite;

    @Value("${file.minio.readPath}")
    private String readPath;

    @Autowired
    private GeneratePageService generatePageService;


    /**
     * 发表文章
     *
     * @param newsId 文章id
     */
    @Override
    @GlobalTransactional(rollbackFor = Exception.class, timeoutMills = 100000)
    public void publishArticle(Integer newsId) {
        // 查询文章
        WmNews wmNews = getWmNews(newsId);
        // 封装 ApArticle
        ApArticle apArticle = getApArticle(wmNews);
        // 保存或修改ApArticle
        saveOrUpdateApArticle(apArticle);
        // 保存关联配置和内容信息
        saveConfigAndContent(wmNews, apArticle);
        // 页面静态化
        generatePageService.generateArticlePage(wmNews.getContent(), apArticle);
        // 修改文章状态
        updateWmNews(newsId, wmNews, apArticle);
        // todo 通知es索引库添加文章索引
    }

    /**
     * 修改文章状态
     *
     * @param newsId    id
     * @param wmNews    wmNews
     * @param apArticle apArticle
     */
    private void updateWmNews(Integer newsId, WmNews wmNews, ApArticle apArticle) {
        wmNews.setStatus(WmNews.Status.PUBLISHED.getCode());
        wmNews.setArticleId(apArticle.getId());
        ResponseResult updateResult = wemediaFeign.updateWmNews(wmNews);
        if (!updateResult.checkCode()) {
            log.error("文章发布失败 远程调用修改文章接口失败， 不予发布 , 文章id : {} ", newsId);
            CustException.cust(AppHttpCodeEnum.REMOTE_SERVER_ERROR, "远程调用修改文章接口失败");
        }
    }

    /**
     * 保存关联配置和内容信息
     *
     * @param wmNews    wmNews
     * @param apArticle apArticle
     */
    private void saveConfigAndContent(WmNews wmNews, ApArticle apArticle) {
        // 保存配置
        ApArticleConfig articleConfig = new ApArticleConfig();
        articleConfig.setArticleId(apArticle.getId());
        articleConfig.setIsComment(true);
        articleConfig.setIsDown(false);
        articleConfig.setIsDelete(false);
        articleConfig.setIsForward(true);
        articleConfigMapper.insert(articleConfig);
        // 保存内容
        ApArticleContent articleContent = new ApArticleContent();
        articleContent.setArticleId(apArticle.getId());
        articleContent.setContent(wmNews.getContent());
        articleContentMapper.insert(articleContent);
    }

    /**
     * 保存或修改ApArticle
     *
     * @param apArticle apArticle
     */
    private void saveOrUpdateApArticle(ApArticle apArticle) {
        if (apArticle.getId() == null) {
            // 保存
            apArticle.setLikes(0);
            apArticle.setCollection(0);
            apArticle.setComment(0);
            apArticle.setViews(0);
            save(apArticle);
        } else {
            // 修改
            ApArticle article = baseMapper.selectById(apArticle.getId());
            if (article == null) {
                CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "关联的文章不存在");
            }
            updateById(apArticle);
            // 删除文章内容
            articleContentMapper.delete(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, apArticle.getId()));
            // 删除文章配置
            articleConfigMapper.delete(Wrappers.<ApArticleConfig>lambdaQuery().eq(ApArticleConfig::getArticleId, apArticle.getId()));

        }
    }

    /**
     * 封装 ApArticle
     *
     * @param wmNews 文章
     * @return {@code ApArticle}
     */
    private ApArticle getApArticle(WmNews wmNews) {
        ApArticle apArticle = new ApArticle();
        apArticle.setTitle(wmNews.getTitle());
        apArticle.setLabels(wmNews.getLabels());
        apArticle.setCreatedTime(new Date());
        apArticle.setPublishTime(wmNews.getPublishTime());
        apArticle.setImages(wmNews.getImages());
        apArticle.setId(wmNews.getArticleId());
        apArticle.setFlag((byte) 0);
        apArticle.setLayout(wmNews.getType());
        // 远程调用admin获取频道信息
        ResponseResult<AdChannel> responseResult = adminFeign.findOne(wmNews.getChannelId());
        if (!responseResult.checkCode()) {
            log.error("发布文章失败:远程调用查询频道出现异常,不予发布,文章id:{}频道id:{}", wmNews.getId(), wmNews.getChannelId());
            CustException.cust(AppHttpCodeEnum.REMOTE_SERVER_ERROR, "发布文章失败:远程调用查询频道出现异常");
        }
        AdChannel adChannel = responseResult.getData();
        if (adChannel == null) {
            log.error("文章发布失败:未查询到相关频道信息,不予发布,文章id:{}频道id:{}", wmNews.getId(), wmNews.getChannelId());
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "发布文章失败:未查询到相关频道信息");
        }
        apArticle.setChannelId(adChannel.getId());
        apArticle.setChannelName(adChannel.getName());
        // 获取用户信息
        ApAuthor apAuthor = authorMapper.selectOne(Wrappers.<ApAuthor>lambdaQuery().eq(ApAuthor::getWmUserId, wmNews.getUserId()));
        if (apAuthor == null) {
            log.error("发布文章失败:未查询到相关作者信息,不予发布,文章id:{}自媒体用户id:{}", wmNews.getId(), wmNews.getUserId());
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "发布文章失败:根据自媒体用户,查询关联作者信息失败");
        }
        apArticle.setAuthorName(apAuthor.getName());
        apArticle.setAuthorId(Long.valueOf(apAuthor.getId()));
        return apArticle;
    }

    /**
     * 查询并检查自媒体文章
     *
     * @param newsId id
     * @return {@code WmNews}
     */
    private WmNews getWmNews(Integer newsId) {
        if (newsId == null) {
            log.error("发布文章失败,文章id为空");
            CustException.cust(AppHttpCodeEnum.PARAM_REQUIRE, "发布文章失败:文章id为空");
        }
        ResponseResult<WmNews> newsResponseResult = wemediaFeign.findWmNewsById(newsId);
        if (!newsResponseResult.checkCode()) {
            log.error("发布文章失败:远程调用自媒体文章接口失败,文章id: {}", newsId);
            CustException.cust(AppHttpCodeEnum.REMOTE_SERVER_ERROR, "远程调用自媒体文章接口失败");
        }
        WmNews wmNews = newsResponseResult.getData();
        if (wmNews == null) {
            log.error("发布文章失败:未查询到自媒体文章,文章id: {}", newsId);
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "发布文章失败:未查询到自媒体文章");
        }
        if (WmNews.Status.ADMIN_SUCCESS.getCode() != wmNews.getStatus() && WmNews.Status.SUCCESS.getCode() != wmNews.getStatus()) {
            log.error("发布文章失败:文章状态不为 4 或 8不予发布,文章id : {}", newsId);
            CustException.cust(AppHttpCodeEnum.DATA_NOT_ALLOW, "发布文章失败:自媒体文章状态错误");
        }
        return wmNews;
    }

    /**
     * 加载文章列表
     *
     * @param loadType 0为加载更多  1为加载最新
     * @param dto      dto
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult load(Short loadType, ArticleHomeDTO dto) {
        // 页大小
        Integer size = dto.getSize();
        if (size == null || size <= 0) {
            size = 10;
        }
        dto.setSize(size);
        // 频道
        if (StringUtils.isBlank(dto.getTag())) {
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }
        // 时间
        if (dto.getMaxBehotTime() == null) {
            dto.setMaxBehotTime(new Date());
        }
        if (dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date());
        }
        // 类型判断
        if (!loadType.equals(ArticleConstants.LOADTYPE_LOAD_MORE) && !loadType.equals(ArticleConstants.LOADTYPE_LOAD_NEW)) {
            loadType = ArticleConstants.LOADTYPE_LOAD_MORE;
        }
        //2 执行查询
        List<ApArticle> articleList = apArticleMapper.loadArticleList(dto, loadType);
        for (ApArticle article : articleList) {
            // 获取文章封面字段
            String images = article.getImages();
            if (StringUtils.isNotBlank(images)) {
                // 将封面按照,号切割   生成流
                images = Arrays.stream(images.split(","))
                        // 每一个路径添加前缀
                        .map(url -> webSite + url)
                        // 将加了前缀的路径  拼接成字符串
                        .collect(Collectors.joining(","));
                article.setImages(images);
            }

            article.setStaticUrl(readPath + article.getStaticUrl());
        }
        //3 返回结果
        ResponseResult result = ResponseResult.okResult(articleList);
        return result;
    }
}
