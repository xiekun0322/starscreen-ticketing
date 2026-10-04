package com.starscreen.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 最受期待
 */
@Data
@Entity
@Table(name = "expected_movie")
public class ExpectedMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rankNum;      // 排名 1~3
    private String movieTitle;    // 电影名
    private Integer wantCount;    // 想看人数
}
