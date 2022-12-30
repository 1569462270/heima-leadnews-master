package com.heima.model.user.dto;

import lombok.Data;

/**
 * @Author : MR.wu
 * @Description : 用户关注关系dto
 * @Date : 2022/12/30 11:30
 * @Version : 1.0
 */
@Data
public class UserRelationDTO {

    // 文章作者ID
    Integer authorId;

    // 作者对应的apUserId
    Integer authorApUserId;
    // 文章id
    Long articleId;
    /**
     * 操作方式
     * 0  关注
     * 1  取消
     */
    Short operation;
}
