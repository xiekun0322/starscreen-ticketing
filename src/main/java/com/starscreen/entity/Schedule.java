package com.starscreen.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 【功能】场次实体，映射数据库表 schedule。
 *         表示"某部电影在某个影院的某个影厅、某个时间放映"。
 *
 * 【表结构】
 *          schedule(id, movie_id, cinema_id, cinema_name, hall_name,
 *                   start_time, end_time, language, price, date)
 *
 * 【调用方】
 *          - DataInitializer          启动时批量插入
 *          - ScheduleRepository       持久化
 *          - ScheduleService          管理端增删改
 *          - PageController           选影院页、选座页读数据
 *          - OrderService             下单时读价格/场次信息
 *
 * 【被调用】
 *          无（叶子实体）
 *
 * 【注意：没有 movieTitle 字段】
 *          admin-schedules.html 前端代码写了 ${s.movieTitle}，
 *          但本实体没有该字段，所以后台场次列表实际显示的是 "电影 5" 这种占位文本。
 *          修复方式：加 ScheduleVO（带 movieTitle），或前端自己用 movies 列表映射。
 */
@Data
@Entity
@Table(name = "schedule")
public class Schedule {

    /** 主键，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联的电影 ID（不是外键约束，仅存值） */
    private Long movieId;

    /** 影院 ID（演示项目只用到 1/2/3） */
    private Long cinemaId;

    /** 影院名称，如 "厦门华侨大学店" */
    private String cinemaName;

    /** 影厅名，如 "1号厅"、"2号厅"、"3号厅"、"IMAX厅" */
    private String hallName;

    /** 开始时间，字符串格式 "HH:mm"，如 "10:30" */
    private String startTime;

    /** 散场时间，字符串格式 "HH:mm" */
    private String endTime;

    /** 语言版本，如 "国语 2D" */
    private String language;

    /** 票价（单价，下单时 totalPrice = price × 座位数） */
    private Double price;

    /** 放映日期，字符串格式 "yyyy-MM-dd" */
    private String date;
}