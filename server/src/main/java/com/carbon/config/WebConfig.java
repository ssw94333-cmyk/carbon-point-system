package com.carbon.config;

import com.carbon.interceptor.AdminInterceptor;
import com.carbon.interceptor.AuthInterceptor;
import com.carbon.interceptor.RuleManagementInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private AuthInterceptor authInterceptor;
    
    @Autowired
    private AdminInterceptor adminInterceptor;
    
    @Autowired
    private RuleManagementInterceptor ruleManagementInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 认证拦截器
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/register",
                        "/api/user/login",
                        "/api/upload/avatar"  // 注册时上传头像不需要登录
                );
        
        // 管理员权限拦截器 - 只拦截特定的管理员接口
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns(
                        "/api/user/list",              // 用户列表（旧接口，保留兼容）
                        "/api/behavior/audit/**",      // 行为审核
                        "/api/behavior/admin/**",      // 管理员统计等功能
                        "/api/admin/**"                // 所有管理员接口
                );
        
        // 规则管理权限拦截器 - GET 请求放行，POST/PUT/DELETE 需要管理员权限
        registry.addInterceptor(ruleManagementInterceptor)
                .addPathPatterns("/api/behavior/rules/**");
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
