package com.starscreen.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 【功能】电影实体，映射数据库表 movie。
 *         存储电影的基本信息：标题、海报、评分、导演、主演、时长等。
 *
 * 【表结构】
 *          movie(id, title, poster, score, tag, release_date,
 *                status, want_count, director, actors, duration)
 *
 * 【调用方】
 *          - DataInitializer          启动时批量插入 16 部电影
 *          - MovieRepository          持久化
 *          - MovieService             业务层增删改查
 *          - PageController           首页/详情页读取后传给 Thymeleaf
 *
 * 【被调用】
 *          无（叶子实体，不依赖其它业务类）
 *
 * 【状态字段 status 的取值】
 *          - "showing"   正在热映 → 首页 "正在热映" 区
 *          - "upcoming"  即将上映 → 首页 "即将上映" 区
 *
 * 【Lombok 说明】
 *          @Data 自动生成 getter/setter/toString/equals/hashCode，
 *          不要在业务代码里手动覆写这些方法。
 */
@Data
@Entity
@Table(name = "movie")
public class Movie {

    /** 主键，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 电影名（首页/订单/取票码都会显示） */
    @Column(length = 100)
    private String title;

    /** 海报路径，如 "/images/poster1.jpg"（相对路径，前端拼到 background-image） */
    @Column(length = 500)
    private String poster;

    /** 评分（0.0~10.0），即将上映的电影为 null */
    private Double score;

    /** 标签，如 "2DIMAX"、"3D"、"IMAX"，可为空 */
    @Column(length = 50)
    private String tag;

    /** 上映日期，字符串格式 "yyyy-MM-dd"（演示项目未用 LocalDate） */
    @Column(length = 50)
    private String releaseDate;

    /** 状态：showing / upcoming */
    @Column(length = 20)
    private String status;

    /** 想看人数（即将上映的电影才有意义） */
    private Integer wantCount;

    /** 导演 */
    @Column(length = 100)
    private String director;

    /** 主演，逗号分隔，如 "汤姆·赫兰德,赞达亚" */
    @Column(length = 500)
    private String actors;

    /** 时长，单位分钟 */
    private Integer duration;
}