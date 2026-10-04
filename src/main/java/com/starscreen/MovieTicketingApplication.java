package com.starscreen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 【功能】星幕影票系统的启动入口类。
 *         负责引导 Spring Boot 应用启动，并开启定时任务支持。
 *
 * 【调用方】由 JVM 通过 main 方法启动，例如：
 *             java -jar starscreen-ticketing-1.0.0.jar
 *          或 mvn spring-boot:run
 *
 * 【被调用】
 *          启动后 Spring 容器会自动扫描并加载：
 *          - com.starscreen.config.*      （配置类：CorsConfig、WebConfig、DataInitializer…）
 *          - com.starscreen.controller.*  （控制器）
 *          - com.starscreen.service.*     （业务服务）
 *          - com.starscreen.repository.*  （JPA 仓库）
 *          - com.starscreen.common.GlobalExceptionHandler（全局异常处理）
 *
 * 【@EnableScheduling 说明】
 *          开启 Spring 定时任务支持。
 *          本项目中由 OrderService.cancelExpiredOrders() 使用：
 *          @Scheduled(fixedRate = 30_000) —— 每 30 秒扫描一次超时订单。
 *          若去掉此注解，超时订单将永远不会被自动取消，座位也永远不会释放。
 *
 * 【注意事项】
 *          - 数据库连接配置位于 src/main/resources/application.properties
 *          - 首次启动时 DataInitializer 会自动建表并初始化电影/场次/座位数据
 */
@SpringBootApplication
@EnableScheduling
public class MovieTicketingApplication {

    /**
     * 【功能】程序主入口，启动 Spring Boot 内嵌服务器。
     *
     * 【调用链】JVM → main() → SpringApplication.run() → 容器初始化
     *          → DataInitializer.run() 初始化数据 → 开始监听 8080 端口
     *
     * @param args 命令行参数（本项目中未使用）
     */
    public static void main(String[] args) {
        SpringApplication.run(MovieTicketingApplication.class, args);
    }
}
