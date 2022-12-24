package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.wemedia.dto.WmMaterialDTO;
import com.heima.model.wemedia.entity.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author : MR.wu
 * @Description : 自媒体文章素材service接口
 * @Date : 2022/12/24 15:27
 * @Version : 1.0
 */
public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 上传图片
     *
     * @param multipartFile multipartFile
     * @return {@code ResponseResult}
     */
    ResponseResult uploadPicture(MultipartFile multipartFile);

    /**
     * 素材列表查询
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    ResponseResult findList(WmMaterialDTO dto);

    /**
     * 删除图片
     *
     * @param id id
     * @return {@code ResponseResult}
     */
    ResponseResult delPicture(Integer id);

    /**
     * 收藏与取消收藏
     *
     * @param id   id
     * @param type 类型
     * @return {@code ResponseResult}
     */
    ResponseResult updateStatus(Integer id, Short type);
}
