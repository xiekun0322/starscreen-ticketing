package com.starscreen.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 【功能】CORS（跨域资源共享）全局配置。
 *         浏览器出于同源策略，默认禁止跨域请求。
 *         本项目是前后端同源部署（都在 localhost:8080），
 *         但仍显式声明白名单，防止被其它站点跨域调用 API。
 *
 * 【调用方】
 *          Spring 容器启动时自动加载（实现 WebMvcConfigurer 接口）。
 *          在 Spring MVC 初始化阶段调用 addCorsMappings() 注册 CORS 规则。
 *
 * 【被调用】
 *          CorsRegistry（Spring MVC 提供），无需其它业务类。
 *
 * 【生效范围】
 *          仅对 /api/** 路径生效：
 *          - /api/movies、/api/orders、/api/seats、/api/user、/api/admin/** 等
 *          页面路由（/、/movie/detail、/seat/...）不参与 CORS 处理。
 *
 * 【安全说明】
 *          - 只允许 http://localhost:8080 和 http://127.0.0.1:8080
 *          - allowCredentials(true) 允许携带 Cookie（Session ID 依赖 Cookie）
 *          - 因此不能使用 allowedOrigins("*")，必须显式列出域名
 *
 * 【⚠️ 已知问题】
 *          AdminMovieController、AdminScheduleController 等类上还有 @CrossOrigin 注解，
 *          该注解默认允许所有来源，会与这里的白名单冲突。
 *          生产环境建议删除所有 Controller 上的 @CrossOrigin，统一由本类控制。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 【功能】注册 CORS 映射规则。
     * 【调用链】Spring 启动 → WebMvcConfigurer.addCorsMappings() → 本方法
     *          → 生成 CorsConfiguration → 应用到 /api/** 的所有请求
     * 【参数】registry CORS 注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")                                    // 仅对 API 生效
                .allowedOrigins("http://localhost:8080",
                                "http://127.0.0.1:8080")                   // 白名单
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的 HTTP 方法
                .allowedHeaders("*")                                      // 允许所有请求头
                .allowCredentials(true)                                   // 允许携带 Cookie
                .maxAge(3600);                                            // 预检请求缓存 1 小时
    }
}
