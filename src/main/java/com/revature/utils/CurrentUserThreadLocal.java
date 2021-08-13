package com.revature.utils;

import com.revature.model.UserDetails;

/**
 * 当前用户threadLocal
 *
 *
 * @author di.mao
 * @version 1.0
 * Copyright: Copyright (c) 2020
 * @date 2020/7/29 15:12
 */
public class CurrentUserThreadLocal {

    private static final ThreadLocal<UserDetails> CURRENT_USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void set(UserDetails userDetails) {
        CURRENT_USER_THREAD_LOCAL.set(userDetails);
    }

    public static UserDetails get() {
        return CURRENT_USER_THREAD_LOCAL.get();
    }

    /**
     * 防止内存泄漏
     *
     * @date 2020/7/29 15:13
     * @author di.mao
     */
    public static void remove() {
        CURRENT_USER_THREAD_LOCAL.remove();
    }
}
