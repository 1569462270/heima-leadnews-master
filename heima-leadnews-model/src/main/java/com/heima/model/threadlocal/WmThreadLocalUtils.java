package com.heima.model.threadlocal;

import com.heima.model.wemedia.entity.WmUser;

/**
 * @Author : MR.wu
 * @Description : 自媒体thread local工具类
 * @Date : 2022/12/24 14:03
 * @Version : 1.0
 */
public class WmThreadLocalUtils {

    private final static ThreadLocal<WmUser> userThreadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程中的用户
     *
     * @param user
     */
    public static void setUser(WmUser user) {
        userThreadLocal.set(user);
    }

    /**
     * 获取线程中的用户
     *
     * @return
     */
    public static WmUser getUser() {
        return userThreadLocal.get();
    }

    /**
     * 清空线程中的用户信息
     */
    public static void clear() {
        userThreadLocal.remove();
    }
}
