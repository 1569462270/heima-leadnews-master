package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.exception.CustException;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dto.LoginDTO;
import com.heima.model.user.entity.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author : MR.wu
 * @Description : 用户信息实体类
 * @Date : 2022/12/22 19:02
 * @Version : 1.0
 */
@Service
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {
    /**
     * APP登录
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult login(LoginDTO dto) {
        if (StringUtils.isNotBlank(dto.getPhone()) && StringUtils.isNotBlank(dto.getPassword())) {
            // 查询用户
            ApUser dbUser = baseMapper.selectOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, dto.getPhone()));
            if (dbUser == null) {
                CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "APP用户不存在");
            }
            String dbUserPassword = dbUser.getPassword();
            String salt = dbUser.getSalt();
            String password = DigestUtils.md5DigestAsHex((dto.getPassword() + salt).getBytes());
            if (!dbUserPassword.equals(password)) {
                CustException.cust(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR, "手机号或密码错误");
            }
            Map<String, Object> map = new HashMap<>();
            dbUser.setSalt("");
            dbUser.setPassword("");
            map.put("token", AppJwtUtil.getToken(dbUser.getId().longValue()));
            map.put("user", dbUser);
            return ResponseResult.okResult(map);
        } else {
            if (dto.getEquipmentId() == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
            }
            Map<String, Object> map = new HashMap<>();
            // 通过设备ID登录的 userId存0
            map.put("token", AppJwtUtil.getToken(0L));
            return ResponseResult.okResult(map);
        }
    }
}
