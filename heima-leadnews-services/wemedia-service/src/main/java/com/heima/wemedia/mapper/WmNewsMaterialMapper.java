package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.entity.WmNewsMaterial;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author : MR.wu
 * @Description : 自媒体图文引用素材 Mapper接口
 * @Date : 2022/12/24 20:23
 * @Version : 1.0
 */
public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {

    /**
     * 保存文章和素材的关联关系
     *
     * @param wmMaterialIds 素材id集合
     * @param newsId        文章ID
     * @param type          文章封面类型  0 内容引用  1 封面引用
     */
    void saveRelations(@Param("wmMaterialIds") List<Integer> wmMaterialIds,
                       @Param("newsId") Integer newsId,
                       @Param("type") Short type);
}
