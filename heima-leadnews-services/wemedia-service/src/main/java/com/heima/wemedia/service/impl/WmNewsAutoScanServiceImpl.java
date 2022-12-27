package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.heima.aliyun.GreenImageScan;
import com.heima.aliyun.GreenTextScan;
import com.heima.common.constants.admin.WemediaConstants;
import com.heima.common.exception.CustException;
import com.heima.feign.AdminFeign;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.entity.WmNews;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author : MR.wu
 * @Description : 自媒体文章自动审核service实现类
 * @Date : 2022/12/26 17:59
 * @Version : 1.0
 */
@Service
@Slf4j
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {
    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Value("${file.oss.web-site}")
    private String webSite;

    @Autowired
    private AdminFeign adminFeign;

    @Autowired
    private GreenTextScan greenTextScan;

    @Autowired
    private GreenImageScan greenImageScan;

    /**
     * 自动审核文章
     *
     * @param id 文章id
     */
    @Override
    public void autoScanWmNews(Integer id) {
        log.info("开始自媒体文章自动审核,文章id：{}", id);
        // 参数校验
        if (id == null) {
            log.error("文章自动审核失败：文章id为空");
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID, "文章自动审核失败：文章id为空");
        }
        // 查询文章
        WmNews wmNews = wmNewsMapper.selectById(id);
        if (null == wmNews) {
            log.error("文章自动审核失败：文章不存在,待审核文章id：{}", id);
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "文章自动审核失败：文章不存在");
        }
        // 校验文章状态
        if (WemediaConstants.WM_NEWS_SUMMIT_STATUS.equals(wmNews.getStatus())) {
            // 抽取文章的内容和图片
            Map<String, Object> contentAndImageResult = handleTextAndImages(wmNews);
            // 敏感词过滤
            boolean isSensitive = handleSensitive((String) contentAndImageResult.get("content"), wmNews);
            if (!isSensitive) {
                return;
            }
            log.info("自管理敏感词审核通过");
            // 阿里云文本审核
            boolean isTextScan = handleTextScan((String) contentAndImageResult.get("content"), wmNews);
            if (!isTextScan) {
                return;
            }
            log.info("阿里云文本审核通过");
            // 阿里云图片审核
            Object images = contentAndImageResult.get("images");
            if (images != null) {
                boolean isImageScan = handleImageScan((List<String>) images, wmNews);
                if (!isImageScan) return;
                log.info("阿里云图片审核通过");
            }
            updateWmNews(wmNews, WmNews.Status.SUCCESS.getCode(), "审核成功");
            // todo 通知定时发布文章

        }
    }

    /**
     * 阿里云图片审核
     *
     * @param images 图片
     * @param wmNews 新闻
     * @return boolean
     */
    private boolean handleImageScan(List<String> images, WmNews wmNews) {
        boolean flag = true;
        try {
            Map map = greenImageScan.imageUrlScan(images);
            String suggestion = map.get("suggestion").toString();
            switch (suggestion) {
                case "block":
                    updateWmNews(wmNews, WmNews.Status.FAIL.getCode(), "图片中有违规内容,审核失败");
                    flag = false;
                    break;
                case "review":
                    updateWmNews(wmNews, WmNews.Status.ADMIN_AUTH.getCode(), "图片中有不确定内容，转为人工审核");
                    flag = false;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("阿里云图片审核出现异常 , 原因:{}", e.getMessage());
            updateWmNews(wmNews, WmNews.Status.ADMIN_AUTH.getCode(), "阿里云内容服务异常，转为人工审核");
            flag = false;
        }
        return flag;
    }

    /**
     * 阿里云文本审核
     *
     * @param content 内容
     * @param wmNews  新闻
     * @return boolean
     */
    private boolean handleTextScan(String content, WmNews wmNews) {
        log.info("开始阿里云文本审核,content：{}", content);
        boolean flag = true;
        try {
            Map map = greenTextScan.greenTextScan(content);
            String suggestion = map.get("suggestion").toString();
            switch (suggestion) {
                case "block":
                    updateWmNews(wmNews, WmNews.Status.FAIL.getCode(), "文本中有违规内容，审核失败");
                    flag = false;
                    break;
                case "review":
                    updateWmNews(wmNews, WmNews.Status.ADMIN_AUTH.getCode(), "文本中有不确定内容，转为人工审核");
                    flag = false;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("阿里云文本审核出现异常 , 原因:{}", e.getMessage());
            updateWmNews(wmNews, WmNews.Status.ADMIN_AUTH.getCode(), "阿里云内容服务异常，转为人工审核");
            flag = false;
        }
        return flag;
    }

    /**
     * DFA算法敏感词过滤
     *
     * @param content 内容
     * @param wmNews  自媒体文章
     * @return boolean
     */
    private boolean handleSensitive(String content, WmNews wmNews) {
        boolean flag = true;
        log.info("开始DFA算法敏感词过滤,content：{}", content);
        // 远程调用admin接口
        ResponseResult<List<String>> responseResult = adminFeign.sensitives();
        if (responseResult.getCode().intValue() != 0) {
            log.error("文章自动审核 远程调用admin服务敏感词列表失败");
            CustException.cust(AppHttpCodeEnum.REMOTE_SERVER_ERROR, responseResult.getErrorMessage());
        }
        // 敏感词列表
        List<String> sensitiveList = responseResult.getData();
        // 初始化敏感词
        SensitiveWordUtil.initMap(sensitiveList);
        // 检测敏感词
        Map<String, Integer> result = SensitiveWordUtil.matchWords(content);
        if (!CollectionUtils.isEmpty(result)) {
            // 修改文章状态
            updateWmNews(wmNews, WmNews.Status.FAIL.getCode(), "内容中包含敏感词：" + result);
            flag = false;
        }
        return flag;
    }

    /**
     * 修改文章状态
     *
     * @param wmNews 文章
     * @param status 状态
     * @param reason 失败原因
     */
    private void updateWmNews(WmNews wmNews, short status, String reason) {
        wmNews.setStatus(status);
        wmNews.setReason(reason);
        wmNewsMapper.updateById(wmNews);
    }

    /**
     * 处理文本和图像
     *
     * @param wmNews 自媒体文章
     * @return {@code Map<String, Object>}
     */
    private Map<String, Object> handleTextAndImages(WmNews wmNews) {
        // 结果集Map
        HashMap<String, Object> result = new HashMap<>();
        // 参数校验
        if (StringUtils.isBlank(wmNews.getContent())) {
            log.error("文章自动审核失败：文章内容为空,文章id：{}", wmNews.getId());
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "文章自动审核失败：文章内容为空");
        }
        List<Map> contentMapList = JSONObject.parseArray(wmNews.getContent(), Map.class);
        // 获取文章内容
        String content = contentMapList.stream()
                .filter(map -> "text".equals(map.get("type")))
                .map(map -> map.get("value").toString())
                .collect(Collectors.joining("_hmtt_"));
        // 拼接文章标题
        content = content + "_hmtt_" + wmNews.getTitle();
        result.put("content", content);
        // 获取图片
        List<String> imageList = contentMapList.stream()
                .filter(map -> "image".equals(map.get("type")))
                .map(map -> map.get("value").toString())
                .collect(Collectors.toList());
        String images = wmNews.getImages();
        if (StringUtils.isNotBlank(images)) {
            List<String> urls = Arrays.stream(images.split(","))
                    .map(image -> webSite + image)
                    .collect(Collectors.toList());
            imageList.addAll(urls);
        }
        // 去重
        imageList = imageList.stream().distinct().collect(Collectors.toList());
        result.put("images", imageList);
        return result;
    }
}
