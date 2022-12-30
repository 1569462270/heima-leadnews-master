package com.heima.user.service;

import com.heima.model.common.dto.ResponseResult;
import com.heima.model.user.dto.UserRelationDTO;

/**
 * @Author : MR.wu
 * @Description : 用户关注service
 * @Date : 2022/12/30 11:34
 * @Version : 1.0
 */
public interface ApUserRelationService {

    /**
     * 用户关注/取消关注
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    ResponseResult follow(UserRelationDTO dto);
}
