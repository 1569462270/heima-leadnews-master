package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dto.SensitiveDTO;
import com.heima.model.admin.entity.AdSensitive;
import com.heima.model.common.dto.ResponseResult;

import java.util.List;

/**
 * @Author : MR.wu
 * @Description : 敏感词信息service
 * @Date : 2022/12/20 17:15
 * @Version : 1.0
 */
public interface AdSensitiveService extends IService<AdSensitive> {

    /**
     * 查询敏感词列表
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    ResponseResult list(SensitiveDTO dto);

    /**
     * 新增敏感词
     *
     * @param adSensitive 敏感词
     * @return {@code ResponseResult}
     */
    ResponseResult insert(AdSensitive adSensitive);

    /**
     * 更新敏感词
     *
     * @param adSensitive 敏感词
     * @return {@code ResponseResult}
     */
    ResponseResult update(AdSensitive adSensitive);

    /**
     * 删除敏感词
     *
     * @param id id
     * @return {@code ResponseResult}
     */
    ResponseResult delete(Integer id);

    /**
     * 查询敏感词内容列表
     *
     * @return {@code ResponseResult<List<String>>}
     */
    ResponseResult<List<String>> selectAllSensitives();
}
