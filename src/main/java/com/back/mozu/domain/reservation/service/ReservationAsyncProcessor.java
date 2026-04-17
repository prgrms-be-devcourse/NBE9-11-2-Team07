package com.back.mozu.domain.reservation.service;

import com.back.mozu.domain.reservation.entity.Reservation;
import com.back.mozu.domain.reservation.entity.TimeSlot;
import com.back.mozu.domain.reservation.repository.ReservationRepository;
import com.back.mozu.domain.reservation.repository.TimeSlotRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

// 재고 관리
@Service
@RequiredArgsConstructor
public class ReservationAsyncProcessor {

    private final ReservationRepository reservationRepository;
    private final TimeSlotRepository timeSlotRepository;

    // 예약 처리
    @Async
    @Transactional
    public void processReservation(UUID reservationId, UUID timeSlotId, int guestCount) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();

        try {
            TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId).orElseThrow();
            timeSlot.decreaseStock(guestCount); // 낙관적 락
            reservation.modifyReservation(); // 성공
        } catch (ObjectOptimisticLockingFailureException | IllegalStateException | IllegalArgumentException e) {
            reservation.cancelReservation(); // 실패
        }
    }
}