package com.heima.user.service.impl;

import com.heima.common.constants.admin.UserRelationConstants;
import com.heima.common.exception.CustException;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.threadlocal.AppThreadLocalUtils;
import com.heima.model.user.dto.UserRelationDTO;
import com.heima.model.user.entity.ApUser;
import com.heima.user.service.ApUserRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author : MR.wu
 * @Description : 用户关注serviceImpl
 * @Date : 2022/12/30 11:34
 * @Version : 1.0
 */
@Service
public class ApUserRelationServiceImpl implements ApUserRelationService {
    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 用户关注/取消关注
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult follow(UserRelationDTO dto) {
        // 校验参数
        if (dto.getAuthorApUserId() == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID, "作者对应的userId不存在");
        }
        ApUser loginUser = AppThreadLocalUtils.getUser();
        if (loginUser == null) {
            CustException.cust(AppHttpCodeEnum.NEED_LOGIN, "用户未登录");
        }
        if (dto.getOperation() == null || (dto.getOperation().intValue() != 0 && dto.getOperation().intValue() != 1)) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID, "关注类型错误");
        }
        if (loginUser.getId().equals(dto.getAuthorApUserId())) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_ALLOW,"不可以自己关注自己哦~");
        }
        Double score = redisTemplate.opsForZSet().score(UserRelationConstants.FOLLOW_LIST + loginUser.getId(), dto.getAuthorApUserId().toString());
        if (dto.getOperation().intValue() == 0 && score != null) {
            CustException.cust(AppHttpCodeEnum.DATA_EXIST,"您已关注，请勿重复关注");
        }
        try {
            if (dto.getOperation().intValue() == 0) {
                redisTemplate.opsForZSet().add(UserRelationConstants.FOLLOW_LIST + loginUser.getId(), dto.getAuthorApUserId().toString(), System.currentTimeMillis());
                redisTemplate.opsForZSet().add(UserRelationConstants.FANS_LIST + dto.getAuthorApUserId(), loginUser.getId().toString(), System.currentTimeMillis());

            } else {
                // 取消关注
                redisTemplate.opsForZSet().remove(UserRelationConstants.FOLLOW_LIST + loginUser.getId(), dto.getAuthorApUserId().toString());
                redisTemplate.opsForZSet().remove(UserRelationConstants.FANS_LIST + dto.getAuthorApUserId(), loginUser.getId().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            CustException.cust(AppHttpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult();
    }
}
