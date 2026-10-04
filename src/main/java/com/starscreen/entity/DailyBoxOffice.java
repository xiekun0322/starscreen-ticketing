package com.starscreen.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 今日票房
 */
@Data
@Entity
@Table(name = "daily_box_office")
public class DailyBoxOffice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rankNum;      // 排名 1~5
    private String movieTitle;    // 电影名
    private Double amount;        // 票房（万）
}
