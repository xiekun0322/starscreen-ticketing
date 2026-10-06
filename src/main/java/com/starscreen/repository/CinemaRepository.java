package com.starscreen.repository;

import com.starscreen.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    List<Cinema> findByBrand(String brand);
    List<Cinema> findByDistrict(String district);
}
