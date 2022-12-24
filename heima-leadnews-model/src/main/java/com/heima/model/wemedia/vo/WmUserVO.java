package com.heima.model.wemedia.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author : MR.wu
 * @Description : 自媒体用户登录vo
 * @Date : 2022/12/24 13:51
 * @Version : 1.0
 */
@Data
public class WmUserVO {
    private Integer id;
    private String name;
    private String nickname;
    private String image;
    private String email;
    private Date loginTime;
    private Date createdTime;
}
