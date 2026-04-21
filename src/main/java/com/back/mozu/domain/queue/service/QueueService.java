package com.back.mozu.domain.queue.service;

import com.back.mozu.domain.queue.dto.QueueDto.AttemptRequest;
import com.back.mozu.domain.queue.dto.QueueDto.AttemptResponse;
import com.back.mozu.domain.queue.dto.QueueDto.StatusResponse;
import com.back.mozu.domain.reservation.entity.Reservation;
import com.back.mozu.domain.reservation.entity.ReservationStatus;
import com.back.mozu.domain.reservation.entity.TimeSlot;
import com.back.mozu.domain.reservation.repository.ReservationRepository;
import com.back.mozu.domain.reservation.repository.TimeSlotRepository;
import com.back.mozu.domain.reservation.service.ReservationAsyncProcessor;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.back.mozu.domain.customer.entity.Customer;
import com.back.mozu.domain.customer.service.CustomerService;
import java.time.LocalDateTime;

// 메인 서비스
@Service
@RequiredArgsConstructor
public class QueueService {

    private final ReservationRepository reservationRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ReservationAsyncProcessor asyncProcessor;
    private final CustomerService customerService;

    // 예약을 데이터베이스에 저장하고 비동기 처리
    @Transactional
    public AttemptResponse enqueueAttempt(UUID userId, AttemptRequest request) {

        Customer customer = customerService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (customer.isPenaltyActive(LocalDateTime.now())) {
            throw new IllegalArgumentException("현재 예약이 제한된 사용자입니다.");
        }

        // 예약 인원 검증
        if (request.getGuestCount() < 1) {
            throw new IllegalArgumentException("예약 인원은 1명 이상이어야 합니다.");
        }

        TimeSlot timeSlot = timeSlotRepository.findByDateAndTime(request.getDate(), request.getTime())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 시간대입니다."));

        // CANCELED 상태가 아닌 기존 예약 존재 여부 검사
        boolean isDuplicate = reservationRepository.existsByUserIdAndTimeSlotAndStatusNot(
                userId, timeSlot, ReservationStatus.CANCELED
        );

        if (isDuplicate) {
            throw new IllegalArgumentException("이미 처리 중이거나 완료된 예약이 있습니다.");
        }

        // 객체 생성
        Reservation reservation = Reservation.builder()
                .userId(userId)
                .timeSlot(timeSlot)
                .guestCount(request.getGuestCount())
                .status(ReservationStatus.PENDING)
                .build();

        reservationRepository.save(reservation);

        // 비동기 작업을 데이터베이스 저장이 끝난 이후에 시작
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                asyncProcessor.processReservation(reservation.getId(), timeSlot.getId(), request.getGuestCount());
            }
        });

        return new AttemptResponse(reservation.getId());
    }

    // 현재 상태 응답
    @Transactional
    public StatusResponse getAttemptStatus(UUID attemptId) {
        Reservation reservation = reservationRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약 시도입니다."));

        return new StatusResponse(reservation.getStatus());
    }
}