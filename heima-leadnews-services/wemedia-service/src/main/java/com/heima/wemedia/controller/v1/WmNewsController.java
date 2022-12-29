package com.heima.wemedia.controller.v1;

import com.heima.common.constants.admin.WemediaConstants;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.wemedia.dto.NewsAuthDTO;
import com.heima.model.wemedia.dto.WmNewsDTO;
import com.heima.model.wemedia.dto.WmNewsPageReqDTO;
import com.heima.model.wemedia.entity.WmNews;
import com.heima.wemedia.service.WmNewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author : MR.wu
 * @Description : 自媒体图文内容控制器
 * @Date : 2022/12/25 12:03
 * @Version : 1.0
 */
@Api(value = "自媒体文章管理API", tags = "自媒体文章管理API")
@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {

    @Autowired
    private WmNewsService wmNewsService;

    @ApiOperation("根据条件查询文章列表")
    @PostMapping("/list")
    public ResponseResult findAll(@RequestBody WmNewsPageReqDTO wmNewsPageReqDto) {
        return wmNewsService.findList(wmNewsPageReqDto);
    }

    @ApiOperation(value = "发表文章", notes = "发表文章，保存草稿，修改文章 共用的方法")
    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDTO dto) {
        return wmNewsService.submitNews(dto);
    }

    @ApiOperation(value = "根据id查询自媒体文章")
    @GetMapping("/one/{id}")
    public ResponseResult findWmNewsById(@PathVariable("id") Integer id) {
        return wmNewsService.findWmNewsById(id);
    }

    @ApiOperation(value = "根据id删除自媒体文章")
    @GetMapping("/del_news/{id}")
    public ResponseResult delNews(@PathVariable("id") Integer id) {
        return wmNewsService.delNews(id);
    }

    @ApiOperation(value = "自媒体文章上架或下架", notes = "enable 上架: 1 下架: 0")
    @PostMapping("/down_or_up")
    public ResponseResult downOrUp(@RequestBody WmNewsDTO dto) {
        return wmNewsService.downOrUp(dto);
    }

    @PostMapping("/list_vo")
    @ApiOperation(value = "查询文章列表")
    public ResponseResult findList(@RequestBody NewsAuthDTO dto) {
        return wmNewsService.findList(dto);
    }


    @ApiOperation(value = "查询自媒体文章详情", notes = "返回值带作者信息，主要运营管理端调用")
    @GetMapping("/one_vo/{id}")
    public ResponseResult findWmNewsVo(@PathVariable("id") Integer id) {
        return wmNewsService.findWmNewsVo(id);
    }


    @ApiOperation("人工审核通过 状态:4")
    @PostMapping("/auth_pass")
    public ResponseResult authPass(@RequestBody NewsAuthDTO dto) {
        return wmNewsService.updateStatus(WemediaConstants.WM_NEWS_AUTH_PASS, dto);
    }

    @ApiOperation("人工审核失败 状态:2")
    @PostMapping("/auth_fail")
    public ResponseResult authFail(@RequestBody NewsAuthDTO dto) {
        return wmNewsService.updateStatus(WemediaConstants.WM_NEWS_AUTH_FAIL, dto);
    }

    @ApiOperation("根据id修改自媒体文章")
    @PutMapping("/update")
    public ResponseResult updateWmNews(@RequestBody WmNews wmNews) {
        wmNewsService.updateById(wmNews);
        return ResponseResult.okResult();
    }
}
