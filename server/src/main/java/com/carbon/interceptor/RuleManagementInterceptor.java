package com.carbon.interceptor;

import com.carbon.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 规则管理权限拦截器
 * 只拦截 POST/PUT/DELETE 请求，GET 请求允许所有登录用户访问
 */
@Component
public class RuleManagementInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        // 放行 GET 请求（查询规则列表，所有用户都可以访问）
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        // POST/PUT/DELETE 请求需要管理员权限
        Integer userType = UserContext.getUserType();
        
        if (userType == null || userType != 1) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":403,\"message\":\"无权限访问，需要管理员权限\"}");
            return false;
        }
        
        return true;
    }
}
