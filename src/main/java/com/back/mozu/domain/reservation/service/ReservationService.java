package com.back.mozu.domain.reservation.service;

import com.back.mozu.domain.queue.service.LockService;
import com.back.mozu.domain.reservation.dto.ReservationDto;
import com.back.mozu.domain.reservation.entity.Reservation;
import com.back.mozu.domain.reservation.entity.ReservationStatus;
import com.back.mozu.domain.reservation.entity.TimeSlot;
import com.back.mozu.domain.reservation.repository.ReservationRepository;
import com.back.mozu.domain.reservation.repository.TimeSlotRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final LockService lockService;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional(readOnly = true)
    public List<ReservationDto.Response> getMyReservation(UUID customerId) {
        return reservationRepository.findAllByUserId(customerId).stream()
                .map(ReservationDto.Response::from)
                .toList();
    }

    @Transactional
    public ReservationDto.Response modifyMyReservation(UUID reservationId, UUID customerId, ReservationDto.Request request) {
        String lockKey = "stock:timeslot:" + request.date() + request.time();
        String lockToken = UUID.randomUUID().toString();

        if (!lockService.acquireLock(lockKey, lockToken)) {
            throw new RuntimeException("현재 요청이 많아 수정할 수 없습니다. 잠시 후 시도하세요.");
        }

        try {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다. ID: " + reservationId));

            if (!reservation.getUserId().equals(customerId)) {
                throw new RuntimeException("해당 예약을 수정할 권한이 없습니다.");
            }

            TimeSlot oldTs = timeSlotRepository.findByIdWithLock(reservation.getTimeSlot().getId())
                    .orElseThrow(() -> new RuntimeException("기존 타임슬롯을 찾을 수 없습니다."));

            oldTs.release(reservation.getGuestCount());
            redisTemplate.opsForValue().increment("stock:timeslot:" + oldTs.getId(), reservation.getGuestCount());

            oldTs.occupy(request.guestCount());
            redisTemplate.opsForValue().decrement("stock:timeslot:" + oldTs.getId(), request.guestCount());

            reservation.modifyReservation(oldTs, request.guestCount());

            return ReservationDto.Response.from(reservation);

        } finally {
            lockService.releaseLock(lockKey, lockToken);
        }
    }

    @Transactional
    public ReservationDto.Response cancelMyReservation(UUID customerId, UUID reservationId, String cancelReason) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));

        if (!reservation.getUserId().equals(customerId)) {
            throw new RuntimeException("취소 권한이 없습니다.");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELED ||
                reservation.getStatus() == ReservationStatus.CANCEL_PENDING) {
            throw new RuntimeException("이미 취소 처리된 예약입니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        boolean isPenalty = checkPenalty(customerId, reservation, now);

        if (isPenalty) {
            reservation.pendingCancel(cancelReason, now.plusHours(1));
        } else {
            TimeSlot ts = timeSlotRepository.findByIdWithLock(reservation.getTimeSlot().getId())
                    .orElseThrow(() -> new RuntimeException("타임슬롯 조회 실패"));

            ts.release(reservation.getGuestCount());
            redisTemplate.opsForValue().increment("stock:timeslot:" + ts.getId(), reservation.getGuestCount());

            reservation.cancelReservation(cancelReason);
        }
        return ReservationDto.Response.from(reservation);
    }

    private boolean checkPenalty(UUID customerId, Reservation reservation, LocalDateTime now) {
        if (reservation.getReservationOpenedAt() != null &&
                !now.isAfter(reservation.getReservationOpenedAt().plusMinutes(30))) {
            return true;
        }
        long cancelCount = reservationRepository.countByUserIdAndStatusAndCancelledAtGreaterThanEqual(
                customerId, ReservationStatus.CANCELED, now.minusMonths(3));

        return cancelCount >= 3;
    }
}