package com.heima.model.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : MR.wu
 * @Description : APP 用户登录dto
 * @Date : 2022/12/29 19:28
 * @Version : 1.0
 */
@Data
public class LoginDTO {

    /**
     * 设备id
     */
    @ApiModelProperty("设备id")
    private Integer equipmentId;

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    private String phone;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    private String password;
}
