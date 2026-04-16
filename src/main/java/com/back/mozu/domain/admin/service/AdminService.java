package com.back.mozu.domain.admin.service;

import com.back.mozu.domain.admin.dto.AdminDto;
import com.back.mozu.domain.reservation.entity.Reservation;
import com.back.mozu.domain.reservation.entity.ReservationStatus;
import com.back.mozu.domain.reservation.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final ReservationRepository reservationRepository;

    @Transactional
    public AdminDto.CancelReservationResponse cancelReservation(
            UUID reservationId,
            AdminDto.CancelReservationRequest request
    ) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }

        reservation.cancelReservation();

        return new AdminDto.CancelReservationResponse(
                reservation.getId(),
                reservation.getStatus().name(),
                request.reason(),
                LocalDateTime.now()
        );
    }
}

