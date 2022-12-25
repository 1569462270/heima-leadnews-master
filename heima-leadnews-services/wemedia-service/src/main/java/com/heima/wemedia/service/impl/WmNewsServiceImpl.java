package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.admin.WemediaConstants;
import com.heima.common.exception.CustException;
import com.heima.model.common.dto.PageResponseResult;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.threadlocal.WmThreadLocalUtils;
import com.heima.model.wemedia.dto.WmNewsDTO;
import com.heima.model.wemedia.dto.WmNewsPageReqDTO;
import com.heima.model.wemedia.entity.WmNews;
import com.heima.model.wemedia.entity.WmNewsMaterial;
import com.heima.model.wemedia.entity.WmUser;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmNewsService;
import org.apache.commons.lang3.StringUtils;
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
 * @Description : 自媒体图文内容service 实现类
 * @Date : 2022/12/25 12:02
 * @Version : 1.0
 */
@Service
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Value("${file.oss.web-site}")
    private String webSite;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    /**
     * 文章列表查询
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult findList(WmNewsPageReqDTO dto) {
        if (null == dto) {
            CustException.cust(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        // 参数校验
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
     * 自媒体文章发布
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult submitNews(WmNewsDTO dto) {
        if (StringUtils.isBlank(dto.getContent())) {
            CustException.cust(AppHttpCodeEnum.PARAM_REQUIRE, "文章内容为空");
        }
        WmUser user = WmThreadLocalUtils.getUser();
        if (null == user) {
            CustException.cust(AppHttpCodeEnum.NO_OPERATOR_AUTH, "自媒体用户未登录");
        }
        // 保存或修改文章
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(dto, wmNews);
        // 文章自动布局
        if (WemediaConstants.WM_NEWS_TYPE_AUTO.equals(dto.getType())) {
            wmNews.setType(null);
        }
        // 处理参数，List转为String
        String images = imageListToStr(dto.getImages(), webSite);
        wmNews.setImages(images);
        wmNews.setUserId(user.getId());
        saveWmNews(wmNews);
        // 草稿返回
        if (WemediaConstants.WM_NEWS_DRAFT_STATUS.equals(dto.getStatus())) {
            return ResponseResult.okResult();
        }
        // 抽取文章中关联的图片路径
        List<String> materials = parseContentImages(dto.getContent());
        // 关联文章内容中的图片和素材关系
        if (!CollectionUtils.isEmpty(materials)) {
            saveRelativeInfo(materials, wmNews.getId(), WemediaConstants.WM_CONTENT_REFERENCE);
        }
        // 关联文章封面中的图片和素材关系  封面可能是选择自动或者是无图
        saveRelativeInfoForCover(dto, materials, wmNews);
        return ResponseResult.okResult();
    }

    private void saveRelativeInfoForCover(WmNewsDTO dto, List<String> materials, WmNews wmNews) {
        // 前端用户选择的图
        List<String> images = dto.getImages();
        // 自动获取封面 ****
        if (WemediaConstants.WM_NEWS_TYPE_AUTO.equals(dto.getType())) {
            int materialSize = materials.size();
            // 单图
            if (materialSize > 0 && materialSize <= 2) {
                images = materials.stream().limit(1).collect(Collectors.toList());
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                // 多图
            } else if (materialSize > 2) {
                images = materials.stream().limit(3).collect(Collectors.toList());
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
            } else {  // 无图
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }
            if (images != null && images.size() > 0) {
                // 将图片集合 转为字符串  url1,url2,url3
                wmNews.setImages(imageListToStr(images, webSite));
            }
            updateById(wmNews);
        }
        // 保存图片列表和素材的关系
        if (images != null && images.size() > 0) {
            images = images.stream().map(x -> x.replace(webSite, "").replace(" ", "")).collect(Collectors.toList());
            saveRelativeInfo(images, wmNews.getId(), WemediaConstants.WM_IMAGE_REFERENCE);
        }
    }

    private void saveRelativeInfo(List<String> urls, Integer newsId, Short type) {
        // 查询文章内容中的图片对应的素材ID
        List<Integer> ids = wmMaterialMapper.selectRelationsIds(urls, WmThreadLocalUtils.getUser().getId());
        // 判断素材是否缺失
        if (CollectionUtils.isEmpty(ids) || ids.size() < urls.size()) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "相关素材缺失,保存文章失败");
        }
        // 保存素材关系
        wmNewsMaterialMapper.saveRelations(ids, newsId, type);
    }

    private List<String> parseContentImages(String content) {
        List<Map> contents = JSON.parseArray(content, Map.class);
        // 遍历文章内容   将所有 type为image的 value获取出来  去除前缀路径
        return contents.stream()
                // 过滤type=image所有的集合
                .filter(map -> map.get("type").equals(WemediaConstants.WM_NEWS_TYPE_IMAGE))
                // 获取到image下的value  图片url
                .map(x -> (String) x.get("value"))
                // 图片url去除前缀
                .map(url -> url.replace(webSite, "").replace(" ", ""))
                // 去除重复的路径
                .distinct()
                // stream 转成list集合
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
     * 根据文章id查询文章
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
     * 删除文章
     *
     * @param id id
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult delNews(Integer id) {
        if (id == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID, "文章Id不可缺少");
        }
        WmNews wmNews = getById(id);
        if (wmNews == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "文章不存在");
        }
        // 判断当前文章的状态  status==9  enable == 1
        if (wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())
                && wmNews.getEnable().equals(WemediaConstants.WM_NEWS_UP)) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "文章已发布，不能删除");
        }
        // 去除素材与文章的关系
        wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId, wmNews.getId()));
        // 删除文章
        removeById(wmNews.getId());
        return ResponseResult.okResult();
    }

    /**
     * 上下架
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult downOrUp(WmNewsDTO dto) {
        // 检查参数
        if (dto == null || dto.getId() == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
        }
        Short enable = dto.getEnable();
        if (enable == null ||
                (!WemediaConstants.WM_NEWS_UP.equals(enable) && !WemediaConstants.WM_NEWS_DOWN.equals(enable))) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID, "上下架状态错误");
        }
        // 查询文章
        WmNews wmNews = getById(dto.getId());
        if (wmNews == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "文章不存在");
        }
        // 判断文章是否发布
        if (!wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "当前文章不是发布状态，不能上下架");
        }
        //4.修改文章状态，同步到app端（后期做）TODO
        update(Wrappers.<WmNews>lambdaUpdate().eq(WmNews::getId, dto.getId())
                .set(WmNews::getEnable, dto.getEnable()));
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
