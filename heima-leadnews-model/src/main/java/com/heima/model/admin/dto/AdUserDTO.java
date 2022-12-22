package com.heima.model.admin.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : MR.wu
 * @Description : 用户DTO
 * @Date : 2022/12/20 21:09
 * @Version : 1.0
 */
@Data
public class AdUserDTO {

    @ApiModelProperty("用户名")
    private String name;

    @ApiModelProperty("密码")
    private String password;
}
