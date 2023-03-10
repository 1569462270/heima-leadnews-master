package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.admin.NewsAutoScanConstants;
import com.heima.common.constants.admin.NewsUpOrDownConstants;
import com.heima.common.constants.admin.PublishArticleConstants;
import com.heima.common.constants.admin.WemediaConstants;
import com.heima.common.exception.CustException;
import com.heima.model.common.dto.PageResponseResult;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.threadlocal.WmThreadLocalUtils;
import com.heima.model.wemedia.dto.NewsAuthDTO;
import com.heima.model.wemedia.dto.WmNewsDTO;
import com.heima.model.wemedia.dto.WmNewsPageReqDTO;
import com.heima.model.wemedia.entity.WmNews;
import com.heima.model.wemedia.entity.WmNewsMaterial;
import com.heima.model.wemedia.entity.WmUser;
import com.heima.model.wemedia.vo.WmNewsVO;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author : MR.wu
 * @Description : ?????????????????????service ?????????
 * @Date : 2022/12/25 12:02
 * @Version : 1.0
 */
@Service
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Value("${file.oss.web-site}")
    private String webSite;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private WmUserMapper wmUserMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * ??????????????????
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult findList(WmNewsPageReqDTO dto) {
        if (null == dto) {
            CustException.cust(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        // ????????????
        dto.checkParam();
        LambdaQueryWrapper<WmNews> wrapper = Wrappers.<WmNews>lambdaQuery();
        wrapper.like(StringUtils.isNoneBlank(dto.getKeyword()), WmNews::getTitle, dto.getKeyword());
        wrapper.eq(null != dto.getChannelId(), WmNews::getChannelId, dto.getChannelId());
        wrapper.eq(null != dto.getStatus(), WmNews::getStatus, dto.getStatus());
        WmUser user = WmThreadLocalUtils.getUser();
        if (null == user) {
            CustException.cust(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }
        wrapper.ge(null != dto.getBeginPubDate(), WmNews::getPublishTime, dto.getBeginPubDate());
        wrapper.le(null != dto.getEndPubDate(), WmNews::getPublishTime, dto.getEndPubDate());
        wrapper.eq(WmNews::getUserId, user.getId());
        wrapper.orderByDesc(WmNews::getCreatedTime);
        Page<WmNews> wmNewsPage = new Page<>(dto.getPage(), dto.getSize());
        IPage<WmNews> pageReq = page(wmNewsPage, wrapper);
        PageResponseResult result = new PageResponseResult(dto.getPage(), dto.getSize(), pageReq.getTotal());
        result.setData(pageReq.getRecords());
        result.setHost(webSite);
        return result;
    }

    /**
     * ?????????????????????
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult submitNews(WmNewsDTO dto) {
        if (StringUtils.isBlank(dto.getContent())) {
            CustException.cust(AppHttpCodeEnum.PARAM_REQUIRE, "??????????????????");
        }
        WmUser user = WmThreadLocalUtils.getUser();
        if (null == user) {
            CustException.cust(AppHttpCodeEnum.NO_OPERATOR_AUTH, "????????????????????????");
        }
        // ?????????????????????
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(dto, wmNews);
        // ??????????????????
        if (WemediaConstants.WM_NEWS_TYPE_AUTO.equals(dto.getType())) {
            wmNews.setType(null);
        }
        // ???????????????List??????String
        String images = imageListToStr(dto.getImages(), webSite);
        wmNews.setImages(images);
        wmNews.setUserId(user.getId());
        saveWmNews(wmNews);
        // ????????????
        if (WemediaConstants.WM_NEWS_DRAFT_STATUS.equals(dto.getStatus())) {
            return ResponseResult.okResult();
        }
        // ????????????????????????????????????
        List<String> materials = parseContentImages(dto.getContent());
        // ?????????????????????????????????????????????
        if (!CollectionUtils.isEmpty(materials)) {
            saveRelativeInfo(materials, wmNews.getId(), WemediaConstants.WM_CONTENT_REFERENCE);
        }
        // ?????????????????????????????????????????????  ??????????????????????????????????????????
        saveRelativeInfoForCover(dto, materials, wmNews);
        // ???????????? ????????????
        rabbitTemplate.convertAndSend(NewsAutoScanConstants.WM_NEWS_AUTO_SCAN_QUEUE, wmNews.getId());
        log.info("???????????? ??????????????? ==> ??????:{}, ??????id:{}", NewsAutoScanConstants.WM_NEWS_AUTO_SCAN_QUEUE, wmNews.getId());
        return ResponseResult.okResult();
    }

    private void saveRelativeInfoForCover(WmNewsDTO dto, List<String> materials, WmNews wmNews) {
        // ????????????????????????
        List<String> images = dto.getImages();
        // ?????????????????? ****
        if (WemediaConstants.WM_NEWS_TYPE_AUTO.equals(dto.getType())) {
            int materialSize = materials.size();
            // ??????
            if (materialSize > 0 && materialSize <= 2) {
                images = materials.stream().limit(1).collect(Collectors.toList());
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                // ??????
            } else if (materialSize > 2) {
                images = materials.stream().limit(3).collect(Collectors.toList());
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
            } else {  // ??????
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }
            if (images != null && images.size() > 0) {
                // ??????????????? ???????????????  url1,url2,url3
                wmNews.setImages(imageListToStr(images, webSite));
            }
            updateById(wmNews);
        }
        // ????????????????????????????????????
        if (images != null && images.size() > 0) {
            images = images.stream().map(x -> x.replace(webSite, "").replace(" ", "")).collect(Collectors.toList());
            saveRelativeInfo(images, wmNews.getId(), WemediaConstants.WM_IMAGE_REFERENCE);
        }
    }

    private void saveRelativeInfo(List<String> urls, Integer newsId, Short type) {
        // ?????????????????????????????????????????????ID
        List<Integer> ids = wmMaterialMapper.selectRelationsIds(urls, WmThreadLocalUtils.getUser().getId());
        // ????????????????????????
        if (CollectionUtils.isEmpty(ids) || ids.size() < urls.size()) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "??????????????????,??????????????????");
        }
        // ??????????????????
        wmNewsMaterialMapper.saveRelations(ids, newsId, type);
    }

    private List<String> parseContentImages(String content) {
        List<Map> contents = JSON.parseArray(content, Map.class);
        // ??????????????????   ????????? type???image??? value????????????  ??????????????????
        return contents.stream()
                // ??????type=image???????????????
                .filter(map -> map.get("type").equals(WemediaConstants.WM_NEWS_TYPE_IMAGE))
                // ?????????image??????value  ??????url
                .map(x -> (String) x.get("value"))
                // ??????url????????????
                .map(url -> url.replace(webSite, "").replace(" ", ""))
                // ?????????????????????
                .distinct()
                // stream ??????list??????
                .collect(Collectors.toList());
    }

    private void saveWmNews(WmNews wmNews) {
        wmNews.setCreatedTime(new Date());
        wmNews.setUserId(WmThreadLocalUtils.getUser().getId());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable(WemediaConstants.WM_NEWS_UP);
        if (wmNews.getId() == null) {
            save(wmNews);
        } else {
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId, wmNews.getId()));
            updateById(wmNews);
        }
    }

    private String imageListToStr(List<String> images, String webSite) {
        if (!CollectionUtils.isEmpty(images)) {
            return images.stream().map(url -> url.replace(webSite, "")).collect(Collectors.joining(","));
        }
        return null;
    }


    /**
     * ????????????id????????????
     *
     * @param id id
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult findWmNewsById(Integer id) {
        if (id == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
        }
        WmNews wmNews = getById(id);
        if (wmNews == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        ResponseResult result = ResponseResult.okResult(wmNews);
        result.setHost(webSite);
        return result;
    }

    /**
     * ????????????
     *
     * @param id id
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult delNews(Integer id) {
        if (id == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID, "??????Id????????????");
        }
        WmNews wmNews = getById(id);
        if (wmNews == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "???????????????");
        }
        // ???????????????????????????  status==9  enable == 1
        if (wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())
                && wmNews.getEnable().equals(WemediaConstants.WM_NEWS_UP)) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "??????????????????????????????");
        }
        // ??????????????????????????????
        wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId, wmNews.getId()));
        // ????????????
        removeById(wmNews.getId());
        return ResponseResult.okResult();
    }

    /**
     * ?????????
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult downOrUp(WmNewsDTO dto) {
        // ????????????
        if (dto == null || dto.getId() == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
        }
        Short enable = dto.getEnable();
        if (enable == null ||
                (!WemediaConstants.WM_NEWS_UP.equals(enable) && !WemediaConstants.WM_NEWS_DOWN.equals(enable))) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID, "?????????????????????");
        }
        // ????????????
        WmNews wmNews = getById(dto.getId());
        if (wmNews == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "???????????????");
        }
        // ????????????????????????
        if (!wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "????????????????????????????????????????????????");
        }
        update(Wrappers.<WmNews>lambdaUpdate().eq(WmNews::getId, dto.getId())
                .set(WmNews::getEnable, dto.getEnable()));
        // ?????????article
        if (wmNews.getArticleId() != null) {
            if (WemediaConstants.WM_NEWS_UP.equals(dto.getEnable())) {
                rabbitTemplate.convertAndSend(NewsUpOrDownConstants.NEWS_UP_OR_DOWN_EXCHANGE, NewsUpOrDownConstants.NEWS_UP_ROUTE_KEY, wmNews.getArticleId());
                log.info("??????????????????????????????,??????id???{}", wmNews.getArticleId());
            } else {
                rabbitTemplate.convertAndSend(NewsUpOrDownConstants.NEWS_UP_OR_DOWN_EXCHANGE, NewsUpOrDownConstants.NEWS_DOWN_ROUTE_KEY, wmNews.getArticleId());
                log.info("??????????????????????????????,??????id???{}", wmNews.getArticleId());
            }
        }
        // todo ??????????????????????????????app???
        // todo ???????????????es???article
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * ??????????????????
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult findList(NewsAuthDTO dto) {
        // ????????????
        dto.checkParam();
        // ???????????????
        int currentPage = dto.getPage();
        // ???????????????
        dto.setPage((dto.getPage() - 1) * dto.getSize());
        if (StringUtils.isNotBlank(dto.getTitle())) {
            dto.setTitle("%" + dto.getTitle() + "%");
        }

        // ????????????
        List<WmNewsVO> wmNewsVoList = baseMapper.findListAndPage(dto);
        // ?????????????????????
        long count = baseMapper.findListCount(dto);

        //3.????????????
        ResponseResult result = new PageResponseResult(currentPage, dto.getSize(), count, wmNewsVoList);
        result.setHost(webSite);
        return result;
    }


    /**
     * ????????????
     *
     * @param id id
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult findWmNewsVo(Integer id) {
        // ????????????
        if (id == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // ??????????????????
        WmNews wmNews = getById(id);
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        // ????????????
        WmUser wmUser = null;
        if (wmNews.getUserId() != null) {
            wmUser = wmUserMapper.selectById(wmNews.getUserId());
        }

        // ??????vo????????????
        WmNewsVO wmNewsVo = new WmNewsVO();
        BeanUtils.copyProperties(wmNews, wmNewsVo);
        if (wmUser != null) {
            wmNewsVo.setAuthorName(wmUser.getName());
        }
        ResponseResult responseResult = ResponseResult.okResult(wmNewsVo);
        responseResult.setHost(webSite);
        return responseResult;
    }

    /**
     * ????????????
     *
     * @param status ??????
     * @param dto    dto
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult updateStatus(Short status, NewsAuthDTO dto) {
        // ????????????
        if (dto == null || dto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // ????????????
        WmNews wmNews = getById(dto.getId());
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        // ?????????????????? ?????????9  ?????????
        if (wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_ALLOW, "???????????????");
        }
        // ??????????????????
        wmNews.setStatus(status);
        if (StringUtils.isNotBlank(dto.getMsg())) {
            wmNews.setReason(dto.getMsg());
        }
        updateById(wmNews);

        // ????????????????????????
        // ????????????
        long publishTime = wmNews.getPublishTime().getTime();
        // ????????????
        long now = new Date().getTime();
        long remainTime = publishTime - now;
        rabbitTemplate.convertAndSend(PublishArticleConstants.DELAY_DIRECT_EXCHANGE, PublishArticleConstants.PUBLISH_ARTICLE_ROUTE_KEY, wmNews.getId(), message -> {
            message.getMessageProperties().setHeader("x-delay", remainTime <= 0 ? 0 : remainTime);
            return message;
        });
        log.info("?????????????????????????????????????????????id : {}", wmNews.getId());

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
