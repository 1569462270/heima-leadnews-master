package com.heima.feign;

import com.heima.config.HeimaFeignAutoConfiguration;
import com.heima.feign.fallback.AdminFeignFallback;
import com.heima.model.common.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @Author : MR.wu
 * @Description : admin feign接口
 * @Date : 2022/12/26 12:30
 * @Version : 1.0
 */
@FeignClient(value = "leadnews-admin",
        fallbackFactory = AdminFeignFallback.class,
        configuration = HeimaFeignAutoConfiguration.class
)
public interface AdminFeign {

    @PostMapping("/api/v1/sensitive/sensitives")
    ResponseResult<List<String>> sensitives();
}
