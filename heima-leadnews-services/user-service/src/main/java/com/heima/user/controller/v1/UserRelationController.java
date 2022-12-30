package com.heima.user.controller.v1;

import com.heima.model.common.dto.ResponseResult;
import com.heima.model.user.dto.UserRelationDTO;
import com.heima.user.service.ApUserRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : MR.wu
 * @Description : 用户关注控制器
 * @Date : 2022/12/30 11:31
 * @Version : 1.0
 */
@Api(value = "用户关注API", tags = "用户关注API")
@RestController
@RequestMapping("/api/v1/user")
public class UserRelationController {

    @Autowired
    private ApUserRelationService apUserRelationService;

    @ApiOperation("关注 或 取关")
    @PostMapping("/user_follow")
    public ResponseResult follow(@RequestBody UserRelationDTO dto) {
        return apUserRelationService.follow(dto);
    }
}