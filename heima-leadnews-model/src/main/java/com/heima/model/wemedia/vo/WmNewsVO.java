package com.heima.model.wemedia.vo;

import com.heima.model.wemedia.entity.WmNews;
import lombok.Data;

/**
 * @Author : MR.wu
 * @Description : 自媒体新闻vo
 * @Date : 2022/12/27 20:26
 * @Version : 1.0
 */
@Data
public class WmNewsVO extends WmNews {
    /**
     * 作者名称
     */
    private String authorName;
}
