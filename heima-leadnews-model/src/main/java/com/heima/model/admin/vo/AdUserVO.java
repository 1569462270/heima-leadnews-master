package com.heima.model.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author : MR.wu
 * @Description : 管理员用户vo
 * @Date : 2022/12/20 21:15
 * @Version : 1.0
 */
@Data
public class AdUserVO {
    private Integer id;

    @ApiModelProperty("登录用户名")
    private String name;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("头像")
    private String image;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("最后一次登录时间")
    private Date loginTime;

    @ApiModelProperty("创建时间")
    private Date createdTime;
}
