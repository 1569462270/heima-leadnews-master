package com.heima.common.exception;

import com.heima.model.common.enums.AppHttpCodeEnum;

/**
 * @Author : MR.wu
 * @Description : 自定义异常
 * @Date : 2022/12/20 14:04
 * @Version : 1.0
 */
public class CustomException extends RuntimeException {

    // 异常处理的枚举
    private AppHttpCodeEnum appHttpCodeEnum;

    public CustomException(AppHttpCodeEnum appHttpCodeEnum) {
        this.appHttpCodeEnum = appHttpCodeEnum;
    }

    public CustomException(AppHttpCodeEnum appHttpCodeEnum, String msg) {
        appHttpCodeEnum.setErrorMessage(msg);
        this.appHttpCodeEnum = appHttpCodeEnum;
    }

    public AppHttpCodeEnum getAppHttpCodeEnum() {
        return appHttpCodeEnum;
    }
}
