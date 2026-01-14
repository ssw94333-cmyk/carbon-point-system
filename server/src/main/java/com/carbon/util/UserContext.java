package com.carbon.util;

public class UserContext {
    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<Integer> userTypeHolder = new ThreadLocal<>();
    
    public static void setUserId(Long userId) {
        userIdHolder.set(userId);
    }
    
    public static Long getUserId() {
        return userIdHolder.get();
    }
    
    public static void setUserType(Integer userType) {
        userTypeHolder.set(userType);
    }
    
    public static Integer getUserType() {
        return userTypeHolder.get();
    }
    
    public static void clear() {
        userIdHolder.remove();
        userTypeHolder.remove();
    }
}
