package com.heima.model.wemedia.dto;

import com.heima.model.common.dto.PageRequestDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : MR.wu
 * @Description : 自媒体文章素材列表DTO
 * @Date : 2022/12/24 17:51
 * @Version : 1.0
 */
@Data
public class WmMaterialDTO extends PageRequestDTO {
    /**
     * 是否收藏  1收藏   0未收藏
     */
    @ApiModelProperty("是否收藏  1收藏   0未收藏")
    Short isCollection;
}
