package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.wemedia.dto.WmUserDTO;
import com.heima.model.wemedia.entity.WmUser;

/**
 * @Author : MR.wu
 * @Description : 自媒体用户service接口
 * @Date : 2022/12/22 14:08
 * @Version : 1.0
 */
public interface WmUserService extends IService<WmUser> {

    /**
     * 登录
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    ResponseResult login(WmUserDTO dto);
}
