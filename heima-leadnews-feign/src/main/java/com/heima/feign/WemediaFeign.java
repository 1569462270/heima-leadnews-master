package com.heima.feign;

import com.heima.config.HeimaFeignAutoConfiguration;
import com.heima.feign.fallback.WemediaFeignFallback;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.wemedia.entity.WmNews;
import com.heima.model.wemedia.entity.WmUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author : MR.wu
 * @Description : 自媒体feign接口
 * @Date : 2022/12/22 14:17
 * @Version : 1.0
 */
@FeignClient(
        value = "leadnews-wemedia",
        fallbackFactory = WemediaFeignFallback.class,
        configuration = HeimaFeignAutoConfiguration.class
)
public interface WemediaFeign {

    @PostMapping("/api/v1/user/save")
    ResponseResult<WmUser> save(@RequestBody WmUser wmUser);

    @GetMapping("/api/v1/user/findByName/{name}")
    ResponseResult<WmUser> findByName(@PathVariable("name") String name);

    @GetMapping("/api/v1/news/one/{id}")
    ResponseResult<WmNews> findWmNewsById(@PathVariable("id") Integer id);

    @PutMapping("/api/v1/news/update")
    ResponseResult updateWmNews(@RequestBody WmNews wmNews);
}
