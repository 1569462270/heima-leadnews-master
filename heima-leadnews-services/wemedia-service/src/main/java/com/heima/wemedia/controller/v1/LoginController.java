package com.heima.wemedia.controller.v1;

import com.heima.model.common.dto.ResponseResult;
import com.heima.model.wemedia.dto.WmUserDTO;
import com.heima.wemedia.service.WmUserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : MR.wu
 * @Description : TODO
 * @Date : 2022/12/24 13:54
 * @Version : 1.0
 */
@Api(value = "自媒体用户登录", tags = "自媒体用户登录")
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private WmUserService wmUserService;

    @PostMapping("/in")
    public ResponseResult login(@RequestBody WmUserDTO dto) {
        return wmUserService.login(dto);
    }
}
