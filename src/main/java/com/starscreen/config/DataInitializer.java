package com.starscreen.config;

import com.starscreen.entity.*;
import com.starscreen.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final MovieRepository movieRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final DailyBoxOfficeRepository dailyBoxOfficeRepository;
    private final ExpectedMovieRepository expectedMovieRepository;
    private final Top100MovieRepository top100MovieRepository;
    private final CinemaRepository cinemaRepository;
    private final HallRepository hallRepository;

    private static final Random RANDOM = new Random(2026);

    @Override
    public void run(String... args) throws Exception {

        // ==================== 1. 影院（先建，后面场次要用） ====================
        if (cinemaRepository.count() == 0) {
            log.info("====== 初始化影院数据 ======");
            List<Cinema> cinemas = buildCinemas();
            cinemaRepository.saveAll(cinemas);
            log.info("====== 影院初始化完成：{} 家 ======", cinemas.size());

            // 每家影院建 5 个影厅
            List<Hall> halls = new ArrayList<>();
            for (Cinema c : cinemas) {
                halls.add(buildHall(c.getId(), "1号厅", null, 6, 10));
                halls.add(buildHall(c.getId(), "2号厅", null, 7, 12));
                halls.add(buildHall(c.getId(), "3号厅", "RealD厅", 5, 8));
                halls.add(buildHall(c.getId(), "IMAX厅", "IMAX厅", 10, 16));
                halls.add(buildHall(c.getId(), "VIP厅", "杜比全景声厅", 4, 6));
            }
            hallRepository.saveAll(halls);
            log.info("====== 影厅初始化完成：{} 个 ======", halls.size());
        } else {
            log.info("====== 影院已有数据，跳过 ======");
        }

        // ==================== 2. 电影 ====================
        if (movieRepository.count() == 0) {
            log.info("====== 初始化电影数据 ======");
            List<Movie> movies = new ArrayList<>();

            // 正在热映（8 部）
            movies.add(buildMovie("空枪", "/images/poster1.jpg", 9.5, null, "2026-09-22", "showing", null,
                    "王小明", "张三,李四,王五", 128,
                    "本片讲述了都市青年在一次意外中重拾人生信念的故事。",
                    "剧情,犯罪"));
            movies.add(buildMovie("八仙！", "/images/poster2.jpg", 9.7, "2DIMAX", "2026-07-18", "showing", null,
                    "张艺谋", "何仙姑,吕洞宾,铁拐李,汉钟离", 135,
                    "影片以家喻户晓的传统民间故事为灵感创作，讲述八位仙人各显神通的故事。",
                    "奇幻,喜剧,动作"));
            movies.add(buildMovie("欢迎来龙餐馆", "/images/poster3.jpg", 9.7, "2DIMAX", "2026-09-22", "showing", null,
                    "陈可辛", "葛优,黄渤,沈腾", 145,
                    "一间藏在城市角落的小餐馆，连接着几代人的悲欢离合。",
                    "剧情,喜剧"));
            movies.add(buildMovie("奥德赛", "/images/poster4.jpg", 9.5, "2DIMAX", "2026-09-22", "showing", null,
                    "克里斯托弗·诺兰", "马特·达蒙,汤姆·霍兰德,安妮·海瑟薇", 172,
                    "一场跨越山海的归乡之旅，命运与意志的较量。",
                    "科幻,冒险,剧情"));
            movies.add(buildMovie("复仇者联盟4：终局之战", "/images/poster5.jpg", 9.2, "2DIMAX", "2026-09-22", "showing", null,
                    "罗素兄弟", "小罗伯特·唐尼,克里斯·埃文斯,斯嘉丽·约翰逊", 181,
                    "超级英雄集结，逆转无限战争的最终一战。",
                    "动作,科幻,冒险"));
            movies.add(buildMovie("鸳鸯楼底", "/images/poster6.jpg", 8.9, "3D", "2026-09-22", "showing", null,
                    "杜琪峰", "刘德华,古天乐,张家辉", 118,
                    "老楼里三个家庭的命运，在时代洪流中交织。",
                    "剧情,悬疑"));
            movies.add(buildMovie("痴迷", "/images/poster7.jpg", 9.0, null, "2026-09-22", "showing", null,
                    "李安", "梁朝伟,汤唯,王力宏", 158,
                    "一段关于执念与救赎的都市爱情故事。",
                    "剧情,爱情"));
            movies.add(buildMovie("蜘蛛侠：崭新之日", "/images/poster8.jpg", 9.2, "2DIMAX", "2026-09-22", "showing", null,
                    "乔·沃茨", "汤姆·赫兰德,赞达亚,雅各布·巴特朗", 142,
                    "蜘蛛侠在新时代的全新冒险，面对前所未有的挑战。",
                    "动作,科幻,冒险"));

            // 即将上映（8 部）
            movies.add(buildMovie("水东游", "/images/poster9.jpg", null, null, "2026-10-23", "upcoming", 1238,
                    "宁浩", "徐峥,黄渤,王宝强", 128,
                    "一场关于时间与记忆的奇幻漂流。",
                    "剧情,奇幻"));
            movies.add(buildMovie("追光者", "/images/poster10.jpg", null, null, "2026-10-23", "upcoming", 345,
                    "贾樟柯", "赵涛,廖凡,董子健", 135,
                    "时代浪潮下小人物的坚韧与希望。",
                    "剧情"));
            movies.add(buildMovie("明月照他乡", "/images/poster11.jpg", null, null, "2026-10-24", "upcoming", 726,
                    "李睿珺", "海清,武仁林,杨光锐", 133,
                    "异乡人的孤独与温暖，月光下的相聚与离别。",
                    "剧情,家庭"));
            movies.add(buildMovie("肆条", "/images/poster12.jpg", null, null, "2026-10-24", "upcoming", 639,
                    "宁浩", "黄渤,沈腾,徐峥", 116,
                    "四条命运线交织，一场黑色幽默。",
                    "喜剧,犯罪"));
            movies.add(buildMovie("惊悚的诞生", "/images/poster13.jpg", null, null, "2026-10-24", "upcoming", 345,
                    "温子仁", "帕特里克·威尔森,维拉·法米加", 106,
                    "一部电影的幕后，隐藏着更深的恐怖。",
                    "恐怖,惊悚"));
            movies.add(buildMovie("复仇者联盟4：终局之战", "/images/poster14.jpg", null, "2DIMAX", "2026-10-25", "upcoming", 2239942,
                    "罗素兄弟", "小罗伯特·唐尼,克里斯·埃文斯,斯嘉丽·约翰逊", 181,
                    "超级英雄集结，逆转无限战争的最终一战。",
                    "动作,科幻,冒险"));
            movies.add(buildMovie("老江湖", "/images/poster15.jpg", null, null, "2026-10-25", "upcoming", 68734,
                    "杜琪峰", "古天乐,刘德华,张家辉", 124,
                    "退隐江湖的老大，被迫再度出山。",
                    "动作,犯罪"));
            movies.add(buildMovie("红孩儿火焰山之王", "/images/poster16.jpg", null, null, "2026-10-25", "upcoming", 30942,
                    "饺子", "吕艳婷,囧森瑟夫,瀚墨", 110,
                    "神话史诗的全新演绎，红孩儿的成长之路。",
                    "动画,奇幻"));

            movieRepository.saveAll(movies);
            log.info("====== 电影初始化完成：{} 部 ======", movies.size());
        } else {
            log.info("====== 电影已有数据，跳过 ======");
        }

        // ==================== 3. 场次 ====================
        if (scheduleRepository.count() == 0) {
            log.info("====== 初始化场次数据 ======");
            List<Movie> movies = movieRepository.findAll();
            List<Cinema> cinemas = cinemaRepository.findAll();
            List<Hall> halls = hallRepository.findAll();

            List<Schedule> schedules = new ArrayList<>();
            Random rnd = new Random(2026);

            String[] dates = new String[7];
            for (int i = 0; i < 7; i++) {
                java.time.LocalDateTime d = java.time.LocalDateTime.now().plusDays(i);
                dates[i] = String.format("%04d-%02d-%02d", d.getYear(), d.getMonthValue(), d.getDayOfMonth());
            }

            for (Movie m : movies) {
                int count = 3 + rnd.nextInt(4);   // 每部 3~6 场
                for (int i = 0; i < count; i++) {
                    Cinema c = cinemas.get(rnd.nextInt(cinemas.size()));
                    List<Hall> cinemaHalls = halls.stream()
                            .filter(h -> h.getCinemaId().equals(c.getId()))
                            .toList();
                    Hall h = cinemaHalls.get(rnd.nextInt(cinemaHalls.size()));

                    Schedule s = new Schedule();
                    s.setMovieId(m.getId());
                    s.setCinemaId(c.getId());
                    s.setHallId(h.getId());
                    s.setCinemaName(c.getName());
                    s.setHallName(h.getName());
                    s.setStartTime(generateTime(rnd));
                    s.setEndTime(generateEndTime(s.getStartTime(), m.getDuration() != null ? m.getDuration() : 120));
                    s.setLanguage("国语 2D");
                    s.setPrice(30.0 + rnd.nextInt(30));
                    s.setDate(dates[rnd.nextInt(dates.length)]);
                    schedules.add(s);
                }
            }
            scheduleRepository.saveAll(schedules);
            log.info("====== 场次初始化完成：{} 场 ======", schedules.size());

            // ==================== 4. 座位 ====================
            log.info("====== 开始生成座位数据 ======");
            int totalSeats = 0;
            List<Seat> allSeats = new ArrayList<>();

            for (Schedule s : schedules) {
                int rows = 6, cols = 10;
                if (s.getHallName() != null) {
                    if (s.getHallName().contains("IMAX")) { rows = 10; cols = 16; }
                    else if (s.getHallName().contains("2号")) { rows = 7; cols = 12; }
                    else if (s.getHallName().contains("3号")) { rows = 5; cols = 8; }
                    else if (s.getHallName().contains("VIP")) { rows = 4; cols = 6; }
                }
                for (int r = 1; r <= rows; r++) {
                    for (int c = 1; c <= cols; c++) {
                        Seat seat = new Seat();
                        seat.setScheduleId(s.getId());
                        seat.setRowNum(r);
                        seat.setColNum(c);
                        seat.setStatus(rnd.nextInt(100) < 8 ? "sold" : "available");
                        allSeats.add(seat);

                        if (allSeats.size() >= 2000) {
                            seatRepository.saveAll(allSeats);
                            totalSeats += allSeats.size();
                            allSeats.clear();
                        }
                    }
                }
            }
            if (!allSeats.isEmpty()) {
                seatRepository.saveAll(allSeats);
                totalSeats += allSeats.size();
            }
            log.info("====== 座位初始化完成：{} 个 ======", totalSeats);

        } else {
            log.info("====== 场次已有数据，跳过 ======");
        }

        // ==================== 5. 侧边栏：今日票房 ====================
        if (dailyBoxOfficeRepository.count() == 0) {
            dailyBoxOfficeRepository.save(createBoxOffice(1, "奥德赛", 9256.0));
            dailyBoxOfficeRepository.save(createBoxOffice(2, "欢迎来龙餐馆", 4661.0));
            dailyBoxOfficeRepository.save(createBoxOffice(3, "空枪", 3037.0));
            dailyBoxOfficeRepository.save(createBoxOffice(4, "八仙！", 1736.0));
            dailyBoxOfficeRepository.save(createBoxOffice(5, "密档", 1412.0));
            log.info("====== 今日票房初始化完成 ======");
        }

        // ==================== 6. 侧边栏：最受期待 ====================
        if (expectedMovieRepository.count() == 0) {
            expectedMovieRepository.save(createExpected(1, "复仇者联盟4：终局之战", 2239942));
            expectedMovieRepository.save(createExpected(2, "生化危机：爆发夜", 144997));
            expectedMovieRepository.save(createExpected(3, "神探之痕迹", 91012));
            log.info("====== 最受期待初始化完成 ======");
        }

        // ==================== 7. 侧边栏：TOP 100 ====================
        if (top100MovieRepository.count() == 0) {
            top100MovieRepository.save(createTop100(1, "我不是药神", 9.6));
            top100MovieRepository.save(createTop100(2, "肖申克的救赎", 9.8));
            top100MovieRepository.save(createTop100(3, "海上钢琴师", 9.3));
            log.info("====== TOP 100 初始化完成 ======");
        }

        log.info("====== 数据初始化完成！ ======");
    }

    // ==================== 工具方法 ====================

    private List<Cinema> buildCinemas() {
        List<Cinema> list = new ArrayList<>();
        list.add(buildCinema("厦门万达影城（集美IOI店）", "万达", "集美区",
                "集美区杏林湾路398号IOI Mall 4楼", "退,改签,折扣卡", 1.7));
        list.add(buildCinema("厦门万达影城（湖里万达店）", "万达", "湖里区",
                "湖里区仙岳路4666号万达广场4楼", "退,改签", 5.2));
        list.add(buildCinema("厦门金逸影城（SM广场店）", "金逸", "思明区",
                "思明区嘉禾路468号SM城市广场1期5楼", "退,改签,折扣卡", 8.1));
        list.add(buildCinema("厦门金逸影城（火车站店）", "金逸", "思明区",
                "思明区厦禾路835号罗宾森广场4楼", "退", 6.5));
        list.add(buildCinema("厦门横店影视城（世茂海峡店）", "横店", "思明区",
                "思明区湖滨南路1号世茂海峡大厦4楼", "退,改签", 7.3));
        list.add(buildCinema("厦门CGV影城（万象城店）", "CGV", "思明区",
                "思明区湖滨东路99号华润万象城5楼", "退,改签,折扣卡", 9.8));
        list.add(buildCinema("厦门万象影城（万象城店）", "万象", "思明区",
                "思明区湖滨东路99号华润万象城4楼", "退,改签", 9.8));
        list.add(buildCinema("厦门万象影城（SM新生活广场店）", "万象", "湖里区",
                "湖里区仙岳路4666号SM新生活广场4楼", "退,折扣卡", 5.5));
        list.add(buildCinema("厦门万达影城（杏林店）", "万达", "集美区",
                "集美区杏林湾商务营运中心二期4楼", "退,改签", 3.2));
        list.add(buildCinema("厦门金逸影城（灌口店）", "金逸", "集美区",
                "集美区灌口镇双桥路万科里4楼", "退", 12.5));
        list.add(buildCinema("厦门横店影视城（集美世茂店）", "横店", "集美区",
                "集美区杏林湾路与集美大道交汇处世茂广场4楼", "退,改签,折扣卡", 2.1));
        list.add(buildCinema("厦门中影国际影城（同安店）", "其他", "同安区",
                "同安区银湖路与环城南路交汇处4楼", "退,改签", 15.7));
        list.add(buildCinema("厦门中影国际影城（海沧店）", "其他", "海沧区",
                "海沧区滨湖路1号阿罗海城市广场4楼", "退", 18.9));
        list.add(buildCinema("厦门博纳国际影城（翔安店）", "其他", "翔安区",
                "翔安区新店镇翔安大道1号4楼", "退,改签", 22.1));
        list.add(buildCinema("厦门万达影城（翔安店）", "万达", "翔安区",
                "翔安区马巷镇翔安南路1号4楼", "退,改签", 24.5));
        list.add(buildCinema("厦门金逸影城（同安银城店）", "金逸", "同安区",
                "同安区银城路1号银城广场4楼", "退,折扣卡", 14.3));
        list.add(buildCinema("厦门横店影视城（同安店）", "横店", "同安区",
                "同安区大同街道环城北路1号4楼", "退,改签", 16.8));
        list.add(buildCinema("厦门CGV影城（五缘湾店）", "CGV", "湖里区",
                "湖里区五缘湾道1号建发湾悦城4楼", "退,改签,折扣卡", 11.2));
        list.add(buildCinema("厦门万象影城（集美店）", "万象", "集美区",
                "集美区杏林湾路1号万科里4楼", "退,改签", 1.9));
        list.add(buildCinema("厦门博纳国际影城（思明店）", "其他", "思明区",
                "思明区莲前西路1号瑞景商业广场4楼", "退", 10.5));
        list.add(buildCinema("厦门中影国际影城（湖里店）", "其他", "湖里区",
                "湖里区兴隆路1号4楼", "退,改签", 6.8));
        list.add(buildCinema("厦门金逸影城（海沧店）", "金逸", "海沧区",
                "海沧区海沧大道1号4楼", "退,改签,折扣卡", 19.5));
        list.add(buildCinema("厦门万达影城（海沧店）", "万达", "海沧区",
                "海沧区海沧大道888号4楼", "退,改签", 20.2));
        list.add(buildCinema("厦门横店影视城（翔安店）", "横店", "翔安区",
                "翔安区翔安大道1号4楼", "退", 23.4));
        return list;
    }

    private Cinema buildCinema(String name, String brand, String district,
                                String address, String tags, Double distance) {
        Cinema c = new Cinema();
        c.setName(name);
        c.setBrand(brand);
        c.setDistrict(district);
        c.setAddress(address);
        c.setTags(tags);
        c.setDistance(distance);
        c.setCreateTime(java.time.LocalDateTime.now().toString());
        return c;
    }

    private Hall buildHall(Long cinemaId, String name, String type, int rows, int cols) {
        Hall h = new Hall();
        h.setCinemaId(cinemaId);
        h.setName(name);
        h.setType(type);
        h.setRows(rows);
        h.setCols(cols);
        return h;
    }

    private Movie buildMovie(String title, String poster, Double score, String tag,
                             String releaseDate, String status, Integer wantCount,
                             String director, String actors, Integer duration,
                             String plot, String genres) {
        Movie m = new Movie();
        m.setTitle(title);
        m.setPoster(poster);
        m.setScore(score);
        m.setTag(tag);
        m.setReleaseDate(releaseDate);
        m.setStatus(status);
        m.setWantCount(wantCount);
        m.setDirector(director);
        m.setActors(actors);
        m.setDuration(duration);
        m.setPlot(plot);
        m.setGenres(genres);
        m.setCreateTime(java.time.LocalDateTime.now().toString());
        return m;
    }

    private String generateTime(Random rnd) {
        int hour = 8 + rnd.nextInt(15);
        int minute = rnd.nextBoolean() ? 0 : 30;
        return String.format("%02d:%02d", hour, minute);
    }

    private String generateEndTime(String startTime, int durationMinutes) {
        String[] parts = startTime.split(":");
        int total = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]) + durationMinutes;
        total %= 24 * 60;
        return String.format("%02d:%02d", total / 60, total % 60);
    }

    private DailyBoxOffice createBoxOffice(Integer rank, String title, Double amount) {
        DailyBoxOffice b = new DailyBoxOffice();
        b.setRankNum(rank);
        b.setMovieTitle(title);
        b.setAmount(amount);
        return b;
    }

    private ExpectedMovie createExpected(Integer rank, String title, Integer wantCount) {
        ExpectedMovie e = new ExpectedMovie();
        e.setRankNum(rank);
        e.setMovieTitle(title);
        e.setWantCount(wantCount);
        return e;
    }

    private Top100Movie createTop100(Integer rank, String title, Double score) {
        Top100Movie t = new Top100Movie();
        t.setRankNum(rank);
        t.setMovieTitle(title);
        t.setScore(score);
        return t;
    }
}