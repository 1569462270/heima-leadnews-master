package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.user.dto.LoginDTO;
import com.heima.model.user.entity.ApUser;

/**
 * @Author : MR.wu
 * @Description : 用户信息service
 * @Date : 2022/12/22 19:02
 * @Version : 1.0
 */
public interface ApUserService extends IService<ApUser> {

    /**
     * APP登录
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    ResponseResult login(LoginDTO dto);
}
