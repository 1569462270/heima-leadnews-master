package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.dto.NewsAuthDTO;
import com.heima.model.wemedia.entity.WmNews;
import com.heima.model.wemedia.vo.WmNewsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author : MR.wu
 * @Description : 自媒体图文内容实体类
 * @Date : 2022/12/25 12:01
 * @Version : 1.0
 */
public interface WmNewsMapper extends BaseMapper<WmNews> {

    /**
     * 分页查询
     *
     * @param dto dto
     * @return {@code List<WmNewsVO>}
     */
    List<WmNewsVO> findListAndPage(@Param("dto") NewsAuthDTO dto);

    /**
     * 文章数量
     *
     * @param dto dto
     * @return long
     */
    Long findListCount(@Param("dto") NewsAuthDTO dto);
}
