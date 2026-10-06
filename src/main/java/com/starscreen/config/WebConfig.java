package com.starscreen.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register",
                        "/api/user/send-sms",
                        "/api/user/login-sms",
                        "/api/user/reset-password",
                        "/api/user/current",
                        "/api/seats/**",
                        "/api/movies/**",
                        "/api/schedules/**",
                        "/api/cinemas/**"          // ★ 注意：前面这行末尾必须加逗号
                );
    }
}
