package com.heima.model.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author : MR.wu
 * @Description : 用户信息实体类
 * @Date : 2022/12/22 18:59
 * @Version : 1.0
 */
@Data
@TableName("ap_user")
public class ApUser implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 密码、通信等加密盐
     */
    @TableField("salt")
    @ApiModelProperty("盐")
    private String salt;
    /**
     * 用户名
     */
    @TableField("name")
    @ApiModelProperty("用户名")
    private String name;
    /**
     * 密码,md5加密
     */
    @TableField("password")
    @ApiModelProperty("密码")
    private String password;

    /**
     * 手机号
     */
    @TableField("phone")
    @ApiModelProperty("手机号")
    private String phone;

    /**
     * 头像
     */
    @TableField("image")
    @ApiModelProperty("头像")
    private String image;

    /**
     * 0 男
     1 女
     2 未知
     */
    @TableField("sex")
    @ApiModelProperty("性别 0 男 1 女 2 未知")
    private Boolean sex;

    /**
     * 0 未
     1 是
     */
    @TableField("is_certification")
    private Boolean certification;

    /**
     * 是否身份认证
     */
    @TableField("is_identity_authentication")
    private Boolean identityAuthentication;

    /**
     * 0正常
     1锁定
     */
    @TableField("status")
    private Boolean status;

    /**
     * 0 普通用户
     1 自媒体人
     2 大V
     */
    @TableField("flag")
    private Short flag;

    /**
     * 注册时间
     */
    @TableField("created_time")
    private Date createdTime;
}
