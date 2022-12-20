package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dto.ChannelDTO;
import com.heima.model.admin.entity.AdChannel;
import com.heima.model.common.dto.ResponseResult;

import java.util.List;

/**
 * @Author : MR.wu
 * @Description : 频道信息Service接口
 * @Date : 2022/12/19 23:22
 * @Version : 1.0
 */
public interface AdChannelService extends IService<AdChannel> {

    /**
     * 根据名称分页查询频道列表
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    ResponseResult findByNameAndPage(ChannelDTO dto);

    /**
     * 添加频道
     *
     * @param adChannel 频道
     * @return {@code ResponseResult}
     */
    ResponseResult addChannel(AdChannel adChannel);

    /**
     * 更新频道
     *
     * @param adChannel 频道
     * @return {@code ResponseResult}
     */
    ResponseResult updateChannel(AdChannel adChannel);

    /**
     * 删除通过id
     *
     * @param id id
     * @return {@code ResponseResult}
     */
    ResponseResult deleteById(Integer id);
}
