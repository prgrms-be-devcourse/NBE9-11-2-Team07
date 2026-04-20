package com.back.mozu.domain.reservation.service;

import com.back.mozu.domain.reservation.entity.Reservation;
import com.back.mozu.domain.reservation.entity.ReservationStatus;
import com.back.mozu.domain.reservation.repository.ReservationRepository;
import com.back.mozu.domain.reservation.service.ReleaseScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicReleaseScheduler implements ReleaseScheduler, ApplicationRunner {

    private final TaskScheduler taskScheduler;
    private final ReservationRepository reservationRepository;

    // 서버 시작할 때 자동 실행
    // 왜? 서버 재시작하면 메모리 Task 사라지니까
    // DB에서 CANCEL_PENDING 다시 불러와서 재등록
    @Override
    public void run(ApplicationArguments args) {
        List<Reservation> pendingList = reservationRepository
                .findAllByStatus(ReservationStatus.CANCEL_PENDING);

        log.info("서버 재시작 - CANCEL_PENDING 재등록: {}건", pendingList.size());

        for (Reservation reservation : pendingList) {
            schedule(reservation);
        }
    }

    // 취소 시점에 호출
    // 왜? 정확한 releaseAt 시각에 재고 반환 예약
    @Override
    public void schedule(Reservation reservation) {
        taskScheduler.schedule(
                () -> releaseStock(reservation.getId()),
                reservation.getReleaseAt().toInstant(ZoneOffset.UTC)
        );
        log.info("랜덤 반환 예약 - reservationId: {}, releaseAt: {}",
                reservation.getId(), reservation.getReleaseAt());
    }

    // 실제 재고 반환
    // 왜? DB에서 최신 상태 다시 조회해서 처리
    @Transactional
    public void releaseStock(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow();
        reservation.getTimeSlot().release(reservation.getGuestCount());
        reservation.cancelReservation(reservation.getCancelReason());
        log.info("랜덤 반환 완료 - reservationId: {}", reservationId);
    }
}