package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.user.dto.AuthDTO;
import com.heima.model.user.entity.ApUserRealName;

/**
 * @Author : MR.wu
 * @Description : 用户实名认证service
 * @Date : 2022/12/21 17:59
 * @Version : 1.0
 */
public interface ApUserRealNameService extends IService<ApUserRealName> {

    /**
     * 根据状态查询需要认证相关的用户信息
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    ResponseResult loadListByStatus(AuthDTO dto);


    /**
     * 更新状态通过id
     *
     * @param dto    dto
     * @param status 状态
     * @return {@code ResponseResult}
     */
    ResponseResult updateStatusById(AuthDTO dto, Short status);
}
