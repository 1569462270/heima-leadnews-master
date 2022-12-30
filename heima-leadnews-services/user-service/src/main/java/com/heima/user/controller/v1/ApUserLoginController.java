package com.heima.user.controller.v1;

import com.heima.model.common.dto.ResponseResult;
import com.heima.model.user.dto.LoginDTO;
import com.heima.user.service.ApUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : MR.wu
 * @Description : APP用户登录控制器
 * @Date : 2022/12/29 19:39
 * @Version : 1.0
 */
@Api(value = "app端用户登录api", tags = "app端用户登录api")
@RestController
@RequestMapping("/api/v1/login")
public class ApUserLoginController {

    @Autowired
    private ApUserService userService;

    @ApiOperation("登录")
    @PostMapping("/login_auth")
    public ResponseResult login(@RequestBody LoginDTO dto) {
        return userService.login(dto);
    }


}
