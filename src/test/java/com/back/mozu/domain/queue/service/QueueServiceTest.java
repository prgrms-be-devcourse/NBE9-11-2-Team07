package com.back.mozu.domain.queue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.back.mozu.domain.queue.dto.QueueDto.AttemptRequest;
import com.back.mozu.domain.queue.dto.QueueDto.AttemptResponse;
import com.back.mozu.domain.queue.dto.QueueDto.StatusResponse;
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
import org.junit.jupiter.api.AfterEach;
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

    // 데이터베이스 초기화
    @AfterEach
    void cleanUp() {
        reservationRepository.deleteAllInBatch();
        timeSlotRepository.deleteAllInBatch();
    }

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

    @Test
    @DisplayName("존재하지 않는 타임슬롯 ID로 예약 시도 시 IllegalArgumentException 발생")
    void throwExceptionWhenTimeSlotNotFound() {

        // 랜덤 UUID 1
        UUID fakeTimeSlotId = UUID.randomUUID();

        // 랜덤 UUID 2
        UUID customerId = UUID.randomUUID();

        AttemptRequest request = new AttemptRequest(fakeTimeSlotId, 2);

        assertThatThrownBy(() -> queueService.enqueueAttempt(customerId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 시간대입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 대기 ID로 상태를 조회하면 IllegalArgumentException 발생")
    void throwExceptionWhenAttemptNotFound() {

        // 랜덤 UUID
        UUID fakeAttemptId = UUID.randomUUID();

        assertThatThrownBy(() -> queueService.getAttemptStatus(fakeAttemptId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 예약 시도입니다.");
    }

    @Test
    @DisplayName("남은 재고와 예약 인원 동일 시 성공 및 재고 값 0을 반환")
    void successWhenExactStock() throws InterruptedException {

        // 재고 5개
        TimeSlot timeSlot = TimeSlot.builder()
                .date(LocalDate.now()).time(LocalTime.now()).stock(5).build();
        timeSlotRepository.save(timeSlot);

        // 예약 5명
        AttemptRequest request = new AttemptRequest(timeSlot.getId(), 5);
        AttemptResponse response = queueService.enqueueAttempt(UUID.randomUUID(), request);

        Thread.sleep(2000);

        StatusResponse status = queueService.getAttemptStatus(response.getAttemptId());
        assertThat(status.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);

        TimeSlot updatedSlot = timeSlotRepository.findById(timeSlot.getId()).orElseThrow();
        assertThat(updatedSlot.getStock()).isEqualTo(0);
    }

    @Test
    @DisplayName("남은 재고보다 예약 인원이 많을 시 실패 및 CANCELED 상태 반환")
    void failWhenRequestExceedsStock() throws InterruptedException {

        // 재고 5개
        TimeSlot timeSlot = TimeSlot.builder()
                .date(LocalDate.now()).time(LocalTime.now()).stock(5).build();
        timeSlotRepository.save(timeSlot);

        // 예약 6명
        AttemptRequest request = new AttemptRequest(timeSlot.getId(), 6);
        AttemptResponse response = queueService.enqueueAttempt(UUID.randomUUID(), request);

        Thread.sleep(2000);

        StatusResponse status = queueService.getAttemptStatus(response.getAttemptId());
        assertThat(status.getStatus()).isEqualTo(ReservationStatus.CANCELED);
    }
}