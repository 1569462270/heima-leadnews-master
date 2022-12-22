package com.heima.admin.controller.v1;

import com.heima.admin.service.AdUserService;
import com.heima.model.admin.dto.AdUserDTO;
import com.heima.model.common.dto.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : MR.wu
 * @Description : 管理员用户控制器
 * @Date : 2022/12/20 21:13
 * @Version : 1.0
 */
@Api(value = "运营平台登录API",tags = "运营平台登录API")
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private AdUserService adUserService;

    @ApiOperation("登录")
    @PostMapping("/in")
    public ResponseResult login(@RequestBody AdUserDTO dto) {
        return adUserService.login(dto);
    }
}
