package com.heima.feign.fallback;

import com.heima.feign.ArticleFeign;
import com.heima.model.article.entity.ApAuthor;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author : MR.wu
 * @Description : 文章feign接口降级
 * @Date : 2022/12/22 14:57
 * @Version : 1.0
 */
@Component
@Slf4j
public class ArticleFeignFallback implements FallbackFactory<ArticleFeign> {

    @Override
    public ArticleFeign create(Throwable throwable) {
        return new ArticleFeign() {
            @Override
            public ResponseResult<ApAuthor> findByUserId(Integer userId) {
                log.error("参数 userId : {}", userId);
                log.error("ArticleFeign findByUserId 远程调用出错啦 ~~~ !!!! {} ", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
            }

            @Override
            public ResponseResult save(ApAuthor apAuthor) {
                log.error("参数 apAuthor: {}", apAuthor);
                log.error("ArticleFeign save 远程调用出错啦 ~~~ !!!! {} ", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
            }
        };
    }
}
