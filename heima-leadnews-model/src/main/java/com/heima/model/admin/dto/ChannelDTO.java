package com.heima.model.admin.dto;

import com.heima.model.common.dto.PageRequestDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : MR.wu
 * @Description : 频道信息DTO
 * @Date : 2022/12/19 23:25
 * @Version : 1.0
 */
@Data
public class ChannelDTO extends PageRequestDTO {
    /**
     * 频道名称
     */
    @ApiModelProperty("频道名称")
    private String name;
    /**
     * 频道状态
     */
    @ApiModelProperty("频道状态")
    private Integer status;
}
