package com.back.mozu.domain.reservation.service;

import com.back.mozu.domain.reservation.entity.Reservation;
import com.back.mozu.domain.reservation.entity.ReservationStatus;
import com.back.mozu.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Primary
@Component
@RequiredArgsConstructor
@Slf4j
public class StaticReleaseScheduler implements ReleaseScheduler {

    private final ReservationRepository reservationRepository;

    @Override
    public void schedule(Reservation reservation) {
        // 나중에 구현
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void processRandomRelease() {
        // CANCEL_PENDING + releaseAt 지난 예약 조회
        List<Reservation> pendingList = reservationRepository
                .findAllByStatusAndReleaseAtBefore(
                        ReservationStatus.CANCEL_PENDING,
                        LocalDateTime.now()
                );
        log.info("처리할 CANCEL_PENDING 예약 수: {}", pendingList.size());
        // 재고 반환
        // CANCELED로 변경
        for(Reservation reservation : pendingList){
            reservation.getTimeSlot().release(reservation.getGuestCount());
            reservation.cancelReservation(reservation.getCancelReason());
        }

    }
}