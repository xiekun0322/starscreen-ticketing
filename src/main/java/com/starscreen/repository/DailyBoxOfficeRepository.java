package com.starscreen.repository;

import com.starscreen.entity.DailyBoxOffice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyBoxOfficeRepository extends JpaRepository<DailyBoxOffice, Long> {

    List<DailyBoxOffice> findAllByOrderByRankNumAsc();
}
