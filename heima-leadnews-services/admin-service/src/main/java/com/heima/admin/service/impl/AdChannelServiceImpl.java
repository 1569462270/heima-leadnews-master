package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdChannelMapper;
import com.heima.admin.service.AdChannelService;
import com.heima.model.admin.dto.ChannelDTO;
import com.heima.model.admin.entity.AdChannel;
import com.heima.model.common.dto.PageResponseResult;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author : MR.wu
 * @Description : 频道信息Service实现类
 * @Date : 2022/12/19 23:23
 * @Version : 1.0
 */
@Service
public class AdChannelServiceImpl extends ServiceImpl<AdChannelMapper, AdChannel> implements AdChannelService {

    @Autowired
    private AdChannelMapper adChannelMapper;

    /**
     * 根据名称分页查询频道列表
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult findByNameAndPage(ChannelDTO dto) {
        // 校验参数
        if (null == dto) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 检查分页
        dto.checkParam();
        LambdaQueryWrapper<AdChannel> queryWrapper = Wrappers.<AdChannel>lambdaQuery();
        // 频道名称
        queryWrapper.like(StringUtils.isNoneBlank(dto.getName()), AdChannel::getName, dto.getName());
        // 状态
        queryWrapper.eq(null != dto.getStatus(), AdChannel::getStatus, dto.getStatus());
        // 排序
        queryWrapper.orderByAsc(AdChannel::getOrd);
        // 分页
        Page<AdChannel> channelPage = new Page<>(dto.getPage(), dto.getSize());
        IPage<AdChannel> channelIPage = this.page(channelPage, queryWrapper);
        return new PageResponseResult(dto.getPage(), dto.getSize(), channelIPage.getTotal(), channelIPage.getRecords());
    }

    /**
     * 添加频道
     *
     * @param adChannel 频道
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult addChannel(AdChannel adChannel) {
        if (null == adChannel || StringUtils.isBlank(adChannel.getName())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        if (adChannel.getName().length() > 10) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "频道名称长度不能大于10");
        }

        int count = this.count(Wrappers.<AdChannel>lambdaQuery().eq(AdChannel::getName, adChannel.getName()));
        if (count > 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST, "该频道名称重复");
        }
        adChannel.setCreatedTime(new Date());
        save(adChannel);
        return ResponseResult.okResult();
    }

    /**
     * 更新频道
     *
     * @param adChannel 频道
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult updateChannel(AdChannel adChannel) {
        if (null == adChannel || null == adChannel.getId()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        AdChannel channel = getById(adChannel.getId());
        if (null == channel) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "频道信息不存在");
        }
        if (StringUtils.isNotBlank(adChannel.getName()) && !channel.getName().equals(adChannel.getName())) {
            if (adChannel.getName().length() > 10) {
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "频道名称长度不能大于10");
            }

            int count = count(Wrappers.<AdChannel>lambdaQuery().eq(AdChannel::getName, adChannel.getName()));
            if (count > 0) {
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST, "频道已存在");
            }
        }
        updateById(adChannel);
        return ResponseResult.okResult();
    }

    /**
     * 删除通过id
     *
     * @param id id
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult deleteById(Integer id) {
        if (null == id) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        AdChannel channel = getById(id);
        if (null == channel) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        if (channel.getStatus()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE, "频道启用中无法删除");
        }
        this.removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
