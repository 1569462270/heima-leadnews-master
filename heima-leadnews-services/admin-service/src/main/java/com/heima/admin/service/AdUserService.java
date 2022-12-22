package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dto.AdUserDTO;
import com.heima.model.admin.entity.AdUser;
import com.heima.model.common.dto.ResponseResult;

/**
 * @Author : MR.wu
 * @Description : 管理员用户service接口
 * @Date : 2022/12/20 21:12
 * @Version : 1.0
 */
public interface AdUserService extends IService<AdUser> {

    ResponseResult login(AdUserDTO dto);
}
