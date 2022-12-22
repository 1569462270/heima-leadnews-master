package com.heima.model.wemedia.entity;

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
 * @Description : 自媒体用户实体类
 * @Date : 2022/12/22 14:01
 * @Version : 1.0
 */
@Data
@TableName("wm_user")
public class WmUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Integer id;

    @TableField("ap_user_id")
    @ApiModelProperty("APP用户id")
    private Integer apUserId;

    /**
     * 登录用户名
     */
    @TableField("name")
    @ApiModelProperty("登录用户名")
    private String name;

    /**
     * 登录密码
     */
    @ApiModelProperty("登录密码")
    @TableField("password")
    private String password;

    /**
     * 盐
     */
    @TableField("salt")
    @ApiModelProperty("盐")
    private String salt;

    /**
     * 昵称
     */
    @TableField("nickname")
    @ApiModelProperty("昵称")
    private String nickname;

    /**
     * 头像
     */
    @TableField("image")
    @ApiModelProperty("头像")
    private String image;

    /**
     * 归属地
     */
    @TableField("location")
    @ApiModelProperty("归属地")
    private String location;

    /**
     * 手机号
     */
    @TableField("phone")
    @ApiModelProperty("手机号")
    private String phone;

    /**
     * 状态
     0 暂时不可用
     1 永久不可用
     9 正常可用
     */
    @TableField("status")
    @ApiModelProperty("状态 0 暂时不可用 1 永久不可用 9 正常可用")
    private Integer status;

    /**
     * 邮箱
     */
    @TableField("email")
    @ApiModelProperty("邮箱")
    private String email;

    /**
     * 账号类型
     0 个人
     1 企业
     2 子账号
     */
    @TableField("type")
    @ApiModelProperty("账号类型 0 个人 1 企业 2 子账号")
    private Integer type;

    /**
     * 运营评分
     */
    @TableField("score")
    @ApiModelProperty("运营评分")
    private Integer score;

    /**
     * 最后一次登录时间
     */
    @TableField("login_time")
    @ApiModelProperty("最后一次登录时间")
    private Date loginTime;

    /**
     * 创建时间
     */
    @TableField("created_time")
    @ApiModelProperty("创建时间")
    private Date createdTime;
}
