package com.heima.user.controller.v1;

import com.heima.common.constants.admin.AdminConstants;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.user.dto.AuthDTO;
import com.heima.user.service.ApUserRealNameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : MR.wu
 * @Description : 用户认证控制器
 * @Date : 2022/12/21 18:05
 * @Version : 1.0
 */
@Api(value = "app用户实名认证API", tags = "app用户实名认证API")
@RestController
@RequestMapping("/api/v1/auth")
public class ApUserRealNameController {

    @Autowired
    private ApUserRealNameService userRealNameService;

    @ApiOperation("根据状态查询实名认证列表")
    @PostMapping("/list")
    public ResponseResult loadListByStatus(@RequestBody AuthDTO dto) {
        return userRealNameService.loadListByStatus(dto);
    }


    @ApiOperation("实名认证通过")
    @PostMapping("/authPass")
    public ResponseResult authPass(@RequestBody AuthDTO dto) {
        return userRealNameService.updateStatusById(dto, AdminConstants.PASS_AUTH);
    }

    @ApiOperation("实名认证失败")
    @PostMapping("/authFail")
    public ResponseResult authFail(@RequestBody AuthDTO dto) {
        return userRealNameService.updateStatusById(dto, AdminConstants.FAIL_AUTH);
    }
}
