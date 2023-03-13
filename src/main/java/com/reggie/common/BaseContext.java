package com.reggie.common;

public class BaseContext {
    private final static ThreadLocal<Long> local = new ThreadLocal<>();

    public static void setUserId(long id) {
        local.set(id);
    }

    public static long getUserId() {
        return local.get();
    }
}
