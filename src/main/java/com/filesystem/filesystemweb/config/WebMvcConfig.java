package com.filesystem.filesystemweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description WEB MVC配置 主要是拦截器配置
 * @Author Efar <efarxs@163.com>
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2023/12/28
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 判断是否登录
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                Object user = request.getSession().getAttribute("user");
                if (user == null) {
                    // 未登录，就跳转到登录页面
                    response.sendRedirect("/login");
                    return false;
                }

                return true;
            }
        })
                // 配置不需要走拦截器的路径
                .excludePathPatterns("/login","/register","/captcha")
                .excludePathPatterns("/js/**","/css/**","/layer/**");
    }
}
