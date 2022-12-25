package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.entity.WmMaterial;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author : MR.wu
 * @Description : 自媒体文章素材Mapper接口
 * @Date : 2022/12/24 15:26
 * @Version : 1.0
 */
public interface WmMaterialMapper extends BaseMapper<WmMaterial> {

    /**
     * 根据素材资源路径，查询相关素材id
     *
     * @param urls   素材路径
     * @param userId 用户id
     * @return {@code List<Integer>}
     */
    List<Integer> selectRelationsIds(@Param("urls") List<String> urls,
                                     @Param("userId") Integer userId);
}
