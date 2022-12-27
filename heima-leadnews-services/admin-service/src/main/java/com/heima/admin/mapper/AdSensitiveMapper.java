package com.heima.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.admin.entity.AdSensitive;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author : MR.wu
 * @Description : 敏感词Mapper接口
 * @Date : 2022/12/20 17:14
 * @Version : 1.0
 */
public interface AdSensitiveMapper extends BaseMapper<AdSensitive> {

    @Select("SELECT sensitives FROM ad_sensitive")
    List<String> findAllSensitives();
}
