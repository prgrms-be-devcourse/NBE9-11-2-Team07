package com.back.mozu.domain.queue.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.back.mozu.domain.queue.dto.QueueDto.AttemptRequest;
import com.back.mozu.domain.reservation.entity.ReservationStatus;
import com.back.mozu.domain.reservation.entity.TimeSlot;
import com.back.mozu.domain.reservation.repository.ReservationRepository;
import com.back.mozu.domain.reservation.repository.TimeSlotRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QueueServiceTest {

    @Autowired
    private QueueService queueService;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    // MySQL 환경 필요
    @Test
    @DisplayName("100명이 동시에 10개 남은 좌석을 예약 시 10명만 성공")
    void concurrencyTest() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 테스트용 타임슬롯 재고 10개 생성
        TimeSlot timeSlot = TimeSlot.builder()
                .date(LocalDate.now())
                .time(LocalTime.now())
                .stock(10)
                .build();

        timeSlotRepository.save(timeSlot);
        UUID slotId = timeSlot.getId();

        // 동시에 100개의 예약 시도 요청
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    queueService.enqueueAttempt(UUID.randomUUID(), new AttemptRequest(slotId, 1));
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Thread.sleep(2000);
        int successCount = Math.toIntExact(reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED).count());

        assertThat(successCount).isEqualTo(10);
    }
}