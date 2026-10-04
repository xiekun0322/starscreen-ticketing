package com.starscreen.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * TOP 100 榜单
 */
@Data
@Entity
@Table(name = "top100_movie")
public class Top100Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rankNum;      // 排名 1~3
    private String movieTitle;    // 电影名
    private Double score;         // 评分
}
