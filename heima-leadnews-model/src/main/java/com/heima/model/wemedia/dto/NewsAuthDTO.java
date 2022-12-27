package com.heima.model.wemedia.dto;

import com.heima.model.common.dto.PageRequestDTO;
import lombok.Data;

/**
 * @Author : MR.wu
 * @Description : 文章列表查询dto
 * @Date : 2022/12/27 20:28
 * @Version : 1.0
 */
@Data
public class NewsAuthDTO extends PageRequestDTO {
    /**
     * 文章标题
     */
    private String title;
    /**
     * 状态
     */
    private Short status;

    /**
     * 文章id
     */
    private Integer id;

    /**
     * 失败原因
     */
    private String msg;
}
