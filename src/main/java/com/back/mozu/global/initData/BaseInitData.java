package com.back.mozu.global.initData;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
@RequiredArgsConstructor
@Profile("dev") // 개발 환경에서만 작동
public class BaseInitData {

    @Autowired
    @Lazy
    private BaseInitData self;
    private final UserService userService;
    private final TimeSlotService timeSlotService;
    private final RestaurantSettingService restaurantSettingService;
    private final HolidayService holidayService;

    @Bean
    public ApplicationRunner initData() {
        return args -> {
            self.work1(); // 기초 설정: 유저, 식당 세팅, 휴무일
            self.work2(); // 비즈니스 데이터: 기획서 기반 익월 타임슬롯 생성
        };
    }

    @Transactional
    public void work1() {
        if (userService.count() > 0) return;

        // 1. 레스토랑 기본 세팅 (기획서 기반: 테이블 10개, 영업시간 11:00 ~ 22:00)
        restaurantSettingService.setup(10, LocalTime.of(11, 0), LocalTime.of(22, 0));

        // 2. 테스트 유저 생성
        userService.join("customer@test.com", "1234", "USER");
        userService.join("admin@test.com", "1234", "ADMIN");

        // 3. 2026년 5월 정기 휴무 설정 (매주 일요일, 월요일)
        // 5월 일요일: 3, 10, 17, 24, 31 / 월요일: 4, 11, 18, 25
        int[] sundays = {3, 10, 17, 24, 31};
        int[] mondays = {4, 11, 18, 25};

        for (int d : sundays) holidayService.create(LocalDate.of(2026, 5, d), "정기 휴무(일)");
        for (int d : mondays) holidayService.create(LocalDate.of(2026, 5, d), "정기 휴무(월)");
    }

    @Transactional
    public void work2() {
        if (timeSlotService.count() > 0) return;

        // 4. 익월(2026년 5월) 전체 타임슬롯 생성
        LocalDate start = LocalDate.of(2026, 5, 1);
        LocalDate end = LocalDate.of(2026, 5, 31);

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            // 휴무일이면 생성 건너뛰기
            if (holidayService.isHoliday(date)) continue;

            // 런치 12:00 ~ 14:00 (30분 간격)
            generateTimeSlots(date, LocalTime.of(12, 0), LocalTime.of(14, 0));

            // 디너 17:00 ~ 20:00 (30분 간격)
            generateTimeSlots(date, LocalTime.of(17, 0), LocalTime.of(20, 0));
        }
    }

    // 30분 단위 슬롯 생성을 위한 헬퍼 메서드
    private void generateTimeSlots(LocalDate date, LocalTime start, LocalTime end) {
        LocalTime curr = start;
        while (!curr.isAfter(end)) {
            // 초기 재고는 식당 세팅의 테이블 수(10)로 설정
            timeSlotService.create(date, curr, 10);
            curr = curr.plusMinutes(30);
        }
    }
}
