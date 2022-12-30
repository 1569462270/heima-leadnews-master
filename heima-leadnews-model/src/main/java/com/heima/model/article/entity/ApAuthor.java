package com.heima.model.article.entity;

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
 * @Description : APP文章作者实体类
 * @Date : 2022/12/22 14:48
 * @Version : 1.0
 */
@Data
@TableName("ap_author")
public class ApAuthor implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 作者名称
     */
    @TableField("name")
    @ApiModelProperty("作者名称")
    private String name;
    /**
     * 0 爬取数据
     1 签约合作商
     2 平台自媒体人
     */
    @TableField("type")
    @ApiModelProperty("0 爬取数据 1 签约合作商 2 平台自媒体人")
    private Integer type;
    /**
     * 社交账号ID
     */
    @TableField("user_id")
    @ApiModelProperty("用户id")
    private Integer userId;
    /**
     * 创建时间
     */
    @TableField("created_time")
    @ApiModelProperty("创建时间")
    private Date createdTime;
    /**
     * 自媒体账号
     */
    @TableField("wm_user_id")
    @ApiModelProperty("自媒体账号id")
    private Integer wmUserId;
}
