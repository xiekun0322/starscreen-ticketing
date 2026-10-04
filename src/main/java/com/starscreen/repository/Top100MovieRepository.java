package com.starscreen.repository;

import com.starscreen.entity.Top100Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Top100MovieRepository extends JpaRepository<Top100Movie, Long> {

    List<Top100Movie> findAllByOrderByRankNumAsc();
}
