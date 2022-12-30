package com.heima.article.controller.v1;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.service.ApAuthorService;
import com.heima.model.article.entity.ApAuthor;
import com.heima.model.common.dto.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author : MR.wu
 * @Description : 文章作者控制器
 * @Date : 2022/12/22 14:53
 * @Version : 1.0
 */
@Api(value = "app作者管理API", tags = "app作者管理API")
@RestController
@RequestMapping("/api/v1/author")
public class ApAuthorController {

    @Autowired
    private ApAuthorService authorService;

    @ApiOperation(value = "查询作者", notes = "根据appUserId查询关联作者信息")
    @GetMapping("/findByUserId/{userId}")
    public ResponseResult<ApAuthor> findByUserId(@PathVariable("userId") Integer userId) {
        return ResponseResult.okResult(authorService.getOne(Wrappers.<ApAuthor>lambdaQuery().eq(ApAuthor::getUserId, userId)));
    }

    @ApiOperation(value = "保存作者", notes = "保存作者信息")
    @PostMapping("/save")
    public ResponseResult save(@RequestBody ApAuthor apAuthor) {
        authorService.save(apAuthor);
        return ResponseResult.okResult();
    }
}
