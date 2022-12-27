package com.heima.model.wemedia.dto;

import com.heima.model.common.dto.PageRequestDTO;
import lombok.Data;

import java.util.Date;

/**
 * @Author : MR.wu
 * @Description : 自媒体文章dto
 * @Date : 2022/12/25 12:05
 * @Version : 1.0
 */
@Data
public class WmNewsPageReqDTO extends PageRequestDTO {
    /**
     * 状态
     */

    private Short status;

    /**
     * 开始时间
     */
    private Date beginPubDate;

    /**
     * 结束时间
     */
    private Date endPubDate;

    /**
     * 所属频道ID
     */
    private Integer channelId;

    /**
     * 关键字
     */
    private String keyword;
}
