package com.back.mozu.domain.admin.service;

import com.back.mozu.domain.admin.dto.AdminDto;
import com.back.mozu.domain.admin.dto.AdminReservationDto;
import com.back.mozu.domain.reservation.entity.Reservation;
import com.back.mozu.domain.reservation.entity.ReservationStatus;
import com.back.mozu.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public Page<AdminReservationDto> getReservations(
            LocalDate date,
            LocalTime time,
            String status,
            Pageable pageable) {

        ReservationStatus reservationStatus = status != null ? ReservationStatus.valueOf(status) : null;
        Page<Reservation> reservations = reservationRepository.findAllWithFilters(date, time, reservationStatus, pageable);

        List<AdminReservationDto> dtoList = new ArrayList<>();
        for (Reservation reservation : reservations.getContent()) {
            dtoList.add(new AdminReservationDto(reservation));
        }

        return new PageImpl<>(
                dtoList, pageable, reservations.getTotalElements()
        );
    }

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

        reservation.cancelReservation(request.reason());

        return new AdminDto.CancelReservationResponse(
                reservation.getId(),
                reservation.getStatus().name(),
                request.reason(),
                LocalDateTime.now()
        );
    }
}