package com.heima.common.exception;

import com.heima.model.common.enums.AppHttpCodeEnum;

/**
 * @Author : MR.wu
 * @Description : 异常工具类
 * @Date : 2022/12/20 14:06
 * @Version : 1.0
 */
public class CustException {

    public static void cust(AppHttpCodeEnum codeEnum) {
        throw new CustomException(codeEnum );
    }
    public static void cust(AppHttpCodeEnum codeEnum,String msg) {
        throw new CustomException(codeEnum,msg);
    }
}
