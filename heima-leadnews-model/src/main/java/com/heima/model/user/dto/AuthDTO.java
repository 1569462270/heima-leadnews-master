package com.heima.model.user.dto;

import com.heima.model.common.dto.PageRequestDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : MR.wu
 * @Description : 用户认证DTO
 * @Date : 2022/12/21 17:57
 * @Version : 1.0
 */
@Data
public class AuthDTO extends PageRequestDTO {
    @ApiModelProperty("状态")
    private Short status;

    @ApiModelProperty("认证用户ID")
    private Integer id;

    @ApiModelProperty("驳回的信息")
    private String msg;
}
