package com.starscreen.repository;

import com.starscreen.entity.ExpectedMovie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpectedMovieRepository extends JpaRepository<ExpectedMovie, Long> {

    List<ExpectedMovie> findAllByOrderByRankNumAsc();
}
