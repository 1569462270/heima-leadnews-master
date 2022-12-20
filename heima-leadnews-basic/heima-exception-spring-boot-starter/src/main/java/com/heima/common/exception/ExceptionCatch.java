package com.heima.common.exception;

import com.heima.model.common.dto.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author : MR.wu
 * @Description : 异常处理
 * @Date : 2022/12/20 14:02
 * @Version : 1.0
 */
@Slf4j
@Configuration
@RestControllerAdvice
public class ExceptionCatch {

    @ExceptionHandler(Exception.class)
    public ResponseResult exception(Exception ex) {
        ex.printStackTrace();
        // 记录日志
        log.error("ExceptionCatch ex:{}", ex.getMessage());
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "您的网络异常，请稍后重试");
    }

    @ExceptionHandler(CustomException.class)
    public ResponseResult custException(CustomException ex) {
        ex.printStackTrace();
        log.error("CustomException ex:{}", ex.getMessage());
        AppHttpCodeEnum codeEnum = ex.getAppHttpCodeEnum();
        return ResponseResult.errorResult(codeEnum);
    }
}
