package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.exception.CustException;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dto.PageResponseResult;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.threadlocal.WmThreadLocalUtils;
import com.heima.model.wemedia.dto.WmMaterialDTO;
import com.heima.model.wemedia.entity.WmMaterial;
import com.heima.model.wemedia.entity.WmNewsMaterial;
import com.heima.model.wemedia.entity.WmUser;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author : MR.wu
 * @Description : 自媒体文章素材service实现类
 * @Date : 2022/12/24 15:27
 * @Version : 1.0
 */
@Service
@Slf4j
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {
    @Autowired
    private FileStorageService fileStorageService;

    @Value("${file.oss.prefix}")
    private String prefix;

    @Value("${file.oss.web-site}")
    private String webSite;

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    /**
     * 上传图片
     *
     * @param multipartFile multipartFile
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        // 检验参数
        if (null == multipartFile || multipartFile.getSize() == 0) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID, "请上传正确的文件");
        }
        // 获取用户id
        WmUser user = WmThreadLocalUtils.getUser();
        if (null == user) {
            CustException.cust(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }
        // 校验文件初始名
        String originalFilename = multipartFile.getOriginalFilename();
        if (!checkFileSuffix(originalFilename)) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID, "请上传正确的素材格式,[jpg,jpeg,png,gif]");
        }
        // 上传文件url
        String filePath = "";
        // 文件上传
        String fileName = UUID.randomUUID().toString().replace("-", "");
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        try {
            filePath = fileStorageService.store(prefix, fileName + suffix, multipartFile.getInputStream());
            log.info("阿里云OSS 文件 fileId: {}", filePath);
        } catch (IOException e) {
            log.error("阿里云文件上传失败 uploadPicture error: {}", e.getMessage());
            CustException.cust(AppHttpCodeEnum.SERVER_ERROR, "服务器繁忙请稍后重试");
        }
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setCreatedTime(new Date());
        wmMaterial.setUrl(filePath);
        wmMaterial.setType(Short.valueOf("0"));
        wmMaterial.setIsCollection(Short.valueOf("0"));
        wmMaterial.setUserId(user.getId());
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);
        return ResponseResult.okResult(wmMaterial);
    }

    private boolean checkFileSuffix(String originalFilename) {
        if (StringUtils.isBlank(originalFilename)) {
            return false;
        }
        List<String> allowSuffix = Arrays.asList("jpg", "jpeg", "png", "gif");
        boolean isAllow = false;
        for (String suffix : allowSuffix) {
            if (originalFilename.endsWith(suffix)) {
                isAllow = true;
                break;
            }
        }
        return isAllow;
    }

    /**
     * 素材列表查询
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult findList(WmMaterialDTO dto) {
        dto.checkParam();
        LambdaQueryWrapper<WmMaterial> wrapper = Wrappers.<WmMaterial>lambdaQuery();
        wrapper.eq(null != dto.getIsCollection() && dto.getIsCollection() == 1, WmMaterial::getIsCollection, dto.getIsCollection());
        // 获取用户id
        WmUser user = WmThreadLocalUtils.getUser();
        if (null == user) {
            CustException.cust(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }
        wrapper.eq(WmMaterial::getUserId, user.getId());
        wrapper.orderByDesc(WmMaterial::getCreatedTime);
        Page<WmMaterial> wmMaterialPage = new Page<>(dto.getPage(), dto.getSize());
        IPage<WmMaterial> pageReq = page(wmMaterialPage, wrapper);
        List<WmMaterial> records = pageReq.getRecords();
        for (WmMaterial record : records) {
            record.setUrl(webSite + record.getUrl());
        }
        PageResponseResult pageResponseResult = new PageResponseResult(dto.getPage(), dto.getSize(), pageReq.getTotal());
        pageResponseResult.setData(records);
        return pageResponseResult;
    }


    /**
     * 删除图片
     *
     * @param id id
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult delPicture(Integer id) {
        // 参数校验
        if (id == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 判断当前id是否被引用，如果被使用则禁止删除
        WmMaterial wmMaterial = getById(id);
        if (wmMaterial == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        LambdaQueryWrapper<WmNewsMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WmNewsMaterial::getMaterialId, id);
        Integer count = wmNewsMaterialMapper.selectCount(wrapper);
        if (count > 0) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_ALLOW);
        }
        // 删除素材库
        removeById(id);
        // 删除图片OSS
        fileStorageService.delete(wmMaterial.getUrl());
        // 3 封装结果
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 收藏与取消收藏
     *
     * @param id   id
     * @param type 类型
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult updateStatus(Integer id, Short type) {
        // 检查参数
        if (id == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 更新状态
        WmMaterial material = getById(id);
        if (material == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "素材信息不存在");
        }
        // 获取当前用户信息
        Integer uid = WmThreadLocalUtils.getUser().getId();
        if (!material.getUserId().equals(uid)) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_ALLOW, "只允许收藏自己上传的素材");
        }
        material.setIsCollection(type);
        updateById(material);
        return ResponseResult.okResult();
    }
}
