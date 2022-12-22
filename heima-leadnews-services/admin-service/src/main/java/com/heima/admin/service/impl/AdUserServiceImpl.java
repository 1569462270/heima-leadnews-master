package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdUserMapper;
import com.heima.admin.service.AdUserService;
import com.heima.common.exception.CustException;
import com.heima.common.exception.CustomException;
import com.heima.model.admin.dto.AdUserDTO;
import com.heima.model.admin.entity.AdUser;
import com.heima.model.admin.vo.AdUserVO;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.AppJwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;

/**
 * @Author : MR.wu
 * @Description : 管理员service 实现类
 * @Date : 2022/12/20 21:12
 * @Version : 1.0
 */
@Service
public class AdUserServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements AdUserService {

    @Override
    public ResponseResult login(AdUserDTO dto) {
        // 校验参数
        if (StringUtils.isBlank(dto.getName()) || StringUtils.isBlank(dto.getPassword())) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID, "参数错误");
        }
        // 根据用户名查询用户
        AdUser dbUser = getOne(Wrappers.<AdUser>lambdaQuery().eq(AdUser::getName, dto.getName()));
        if (null == dbUser) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "用户名不存在");
        }
        // 判断用户状态
        if (9 != dbUser.getStatus().intValue()) {
            CustException.cust(AppHttpCodeEnum.LOGIN_STATUS_ERROR, "用户状态异常，请联系管理员");
        }
        String salt = dbUser.getSalt();
        if (!dbUser.getPassword().equals(DigestUtils.md5DigestAsHex((dto.getPassword() + salt).getBytes()))) {
            CustException.cust(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR, "密码错误");
        }
        // 修改登录时间
        dbUser.setLoginTime(new Date());
        updateById(dbUser);
        // token
        String token = AppJwtUtil.getToken(dbUser.getId().longValue());
        // 返回结果vo
        AdUserVO adUserVO = new AdUserVO();
        BeanUtils.copyProperties(dbUser, adUserVO);
        HashMap<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", adUserVO);
        return ResponseResult.okResult(map);
    }
}
