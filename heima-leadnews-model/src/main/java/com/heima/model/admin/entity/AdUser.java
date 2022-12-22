package com.heima.model.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import java.util.Date;

/**
 * @Author : MR.wu
 * @Description : 管理员用户信息实体类
 * @Date : 2022/12/20 20:55
 * @Version : 1.0
 */
@Data
@TableName("ad_user")
public class AdUser {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    /**
     * 登录用户名
     */
    @TableField("name")
    @ApiModelProperty("登录用户名")
    private String name;
    /**
     * 登录密码
     */
    @TableField("password")
    @ApiModelProperty("登录密码")
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
    @ApiModelProperty("状态")
    private Integer status;
    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    @TableField("email")
    @Email(message = "请输入合法的邮箱")
    private String email;
    /**
     * 最后一次登录时间
     */
    @TableField("login_time")
    @ApiModelProperty("最后一次登录时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @TableField("created_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
}
