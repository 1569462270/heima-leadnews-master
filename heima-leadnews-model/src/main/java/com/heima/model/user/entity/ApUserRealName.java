package com.heima.model.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author : MR.wu
 * @Description : APP实名认证信息 实体类
 * @Date : 2022/12/21 11:25
 * @Version : 1.0
 */
@Data
@TableName("ap_user_realname")
public class ApUserRealName implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Integer id;
    /**
     * 账号ID
     */
    @TableField("user_id")
    @ApiModelProperty("账号ID")
    private Integer userId;
    /**
     * 用户名称
     */
    @TableField("name")
    @ApiModelProperty("用户名称")
    private String name;
    /**
     * 身份证号
     */
    @TableField("idno")
    @ApiModelProperty("身份证号")
    private String idno;
    /**
     * 正面照片
     */
    @TableField("font_image")
    @ApiModelProperty("正面照片")
    private String fontImage;
    /**
     * 背面照片
     */
    @TableField("back_image")
    @ApiModelProperty("背面照片")
    private String backImage;
    /**
     * 手持照片
     */
    @TableField("hold_image")
    @ApiModelProperty("手持照片")
    private String holdImage;
    /**
     * 活体照片
     */
    @TableField("live_image")
    @ApiModelProperty("活体照片")
    private String liveImage;
    /**
     * 状态
     0 创建中
     1 待审核
     2 审核失败
     9 审核通过
     */
    @TableField("status")
    @ApiModelProperty("状态 0 创建中 1 待审核 2 审核失败 9 审核通过")
    private Short status;
    /**
     * 拒绝原因
     */
    @TableField("reason")
    @ApiModelProperty("拒绝原因")
    private String reason;
    /**
     * 创建时间
     */
    @TableField("created_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createdTime;
    /**
     * 提交时间
     */
    @TableField("submited_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("提交时间")
    private Date submitedTime;
    /**
     * 更新时间
     */
    @TableField("updated_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updatedTime;
}
