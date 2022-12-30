package com.heima.model.threadlocal;

import com.heima.model.user.entity.ApUser;

/**
 * @Author : MR.wu
 * @Description : APP thread local工具类
 * @Date : 2022/12/29 19:41
 * @Version : 1.0
 */
public class AppThreadLocalUtils {

    private final static ThreadLocal<ApUser> userThreadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程中的用户
     */
    public static void setUser(ApUser user) {
        userThreadLocal.set(user);
    }

    /**
     * 获取线程中的用户
     */
    public static ApUser getUser() {
        return userThreadLocal.get();
    }

    /**
     * 清空线程中的用户信息
     */
    public static void clear() {
        userThreadLocal.remove();
    }
}
