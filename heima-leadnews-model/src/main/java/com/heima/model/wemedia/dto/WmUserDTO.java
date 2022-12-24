package com.heima.model.wemedia.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : MR.wu
 * @Description : 自媒体用户登录DTO
 * @Date : 2022/12/24 13:50
 * @Version : 1.0
 */
@Data
public class WmUserDTO {

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String name;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    private String password;
}
