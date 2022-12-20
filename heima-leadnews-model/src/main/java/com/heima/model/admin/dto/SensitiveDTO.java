package com.heima.model.admin.dto;

import com.heima.model.common.dto.PageRequestDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : MR.wu
 * @Description : 敏感词DTO
 * @Date : 2022/12/20 17:11
 * @Version : 1.0
 */
@Data
public class SensitiveDTO extends PageRequestDTO {
    /**
     * 敏感词名称
     */
    @ApiModelProperty(name = "敏感词名称")
    private String name;
}
