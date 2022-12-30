package com.heima.model.article.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author : MR.wu
 * @Description : 文章dto
 * @Date : 2022/12/29 19:51
 * @Version : 1.0
 */
@Data
public class ArticleHomeDTO {

    // 最大时间
    Date maxBehotTime;
    // 最小时间
    Date minBehotTime;
    // 分页size
    Integer size;
    // 频道ID
    String tag;
}
