package com.starscreen.service;

import com.starscreen.entity.DailyBoxOffice;
import com.starscreen.entity.ExpectedMovie;
import com.starscreen.entity.Top100Movie;
import com.starscreen.repository.DailyBoxOfficeRepository;
import com.starscreen.repository.ExpectedMovieRepository;
import com.starscreen.repository.Top100MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页侧边栏数据服务
 */
@Service
@RequiredArgsConstructor
public class SidebarService {

    private final DailyBoxOfficeRepository dailyBoxOfficeRepository;
    private final ExpectedMovieRepository expectedMovieRepository;
    private final Top100MovieRepository top100MovieRepository;

    /** 今日票房 */
    public List<DailyBoxOffice> getDailyBoxOffice() {
        return dailyBoxOfficeRepository.findAllByOrderByRankNumAsc();
    }

    /** 最受期待 */
    public List<ExpectedMovie> getExpectedMovies() {
        return expectedMovieRepository.findAllByOrderByRankNumAsc();
    }

    /** TOP 100 */
    public List<Top100Movie> getTop100Movies() {
        return top100MovieRepository.findAllByOrderByRankNumAsc();
    }
}
