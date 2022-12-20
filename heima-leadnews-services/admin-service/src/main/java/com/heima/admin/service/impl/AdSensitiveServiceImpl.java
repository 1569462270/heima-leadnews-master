package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdSensitiveMapper;
import com.heima.admin.service.AdSensitiveService;
import com.heima.model.admin.dto.SensitiveDTO;
import com.heima.model.admin.entity.AdSensitive;
import com.heima.model.common.dto.PageResponseResult;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author : MR.wu
 * @Description : 敏感词信息实现类
 * @Date : 2022/12/20 17:16
 * @Version : 1.0
 */
@Service
public class AdSensitiveServiceImpl extends ServiceImpl<AdSensitiveMapper, AdSensitive> implements AdSensitiveService {
    /**
     * 查询敏感词列表
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult list(SensitiveDTO dto) {
        if (null == dto) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        dto.checkParam();
        Page<AdSensitive> sensitivePage = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<AdSensitive> queryWrapper = Wrappers.<AdSensitive>lambdaQuery();
        queryWrapper.like(StringUtils.isNoneBlank(dto.getName()), AdSensitive::getSensitives, dto.getName());
        IPage<AdSensitive> pageReq = page(sensitivePage, queryWrapper);
        return new PageResponseResult(dto.getPage(), dto.getSize(), pageReq.getTotal(), pageReq.getRecords());
    }

    /**
     * 新增敏感词
     *
     * @param adSensitive 敏感词
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult insert(AdSensitive adSensitive) {
        if (null == adSensitive || StringUtils.isBlank(adSensitive.getSensitives())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        int count = this.count(Wrappers.<AdSensitive>lambdaQuery().eq(AdSensitive::getSensitives, adSensitive.getSensitives()));
        if (count > 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST);
        }
        adSensitive.setCreatedTime(new Date());
        this.save(adSensitive);
        return ResponseResult.okResult();
    }

    /**
     * 更新敏感词
     *
     * @param adSensitive 敏感词
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult update(AdSensitive adSensitive) {
        if (null == adSensitive || null == adSensitive.getId()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        AdSensitive sensitive = getById(adSensitive.getId());
        if (null == sensitive) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "敏感词不存在");
        }
        if (StringUtils.isNotBlank(adSensitive.getSensitives()) && !adSensitive.getSensitives().equals(sensitive.getSensitives())) {
            int count = this.count(Wrappers.<AdSensitive>lambdaQuery().eq(AdSensitive::getSensitives, adSensitive.getSensitives()));
            if (count > 0) {
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST, "敏感词已存在");
            }
        }
        updateById(adSensitive);
        return ResponseResult.okResult();
    }

    /**
     * 删除敏感词
     *
     * @param id id
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult delete(Integer id) {
        if (id == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        AdSensitive adSensitive = getById(id);
        if (adSensitive == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
