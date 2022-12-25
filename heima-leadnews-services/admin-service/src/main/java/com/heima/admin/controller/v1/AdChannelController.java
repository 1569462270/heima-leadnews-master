package com.heima.admin.controller.v1;

import com.heima.admin.service.AdChannelService;
import com.heima.model.admin.dto.ChannelDTO;
import com.heima.model.admin.entity.AdChannel;
import com.heima.model.common.dto.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author : MR.wu
 * @Description : 频道信息controller
 * @Date : 2022/12/20 12:10
 * @Version : 1.0
 */
@RequestMapping("/api/v1/channel")
@RestController
@Api(value = "频道管理", tags = "频道管理", description = "频道管理API")
public class AdChannelController {

    @Autowired
    private AdChannelService adChannelService;

    /**
     * 根据名称分页查询频道列表
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    @PostMapping("/list")
    @ApiOperation("频道分页列表查询")
    public ResponseResult list(@RequestBody ChannelDTO dto) {
        return adChannelService.findByNameAndPage(dto);
    }

    /**
     * 添加频道
     *
     * @param adChannel 频道
     * @return {@code ResponseResult}
     */
    @PostMapping("/save")
    @ApiOperation("频道新增")
    public ResponseResult add(@RequestBody AdChannel adChannel) {
        return adChannelService.addChannel(adChannel);
    }

    /**
     * 更新频道
     *
     * @param adChannel 频道
     * @return {@code ResponseResult}
     */
    @PostMapping("/update")
    @ApiOperation("频道修改")
    public ResponseResult update(@RequestBody AdChannel adChannel) {
        return adChannelService.updateChannel(adChannel);
    }

    /**
     * 删除
     *
     * @param id id
     * @return {@code ResponseResult}
     */
    @ApiOperation("根据频道ID删除")
    @GetMapping("/del/{id}")
    public ResponseResult delete(@PathVariable Integer id) {
        return adChannelService.deleteById(id);
    }

    @ApiOperation("查询全部频道")
    @GetMapping("/channels")
    public ResponseResult findAll() {
        List<AdChannel> list = adChannelService.list();
        return ResponseResult.okResult(list);
    }
}
