package com.back.mozu.domain.admin.service;

import com.back.mozu.domain.admin.dto.AdminReservationDto;
import com.back.mozu.domain.reservation.entity.Reservation;
import com.back.mozu.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public Page<AdminReservationDto> getReservations(
            LocalDate date,
            LocalTime time,
            String status,
            Pageable pageable) {

        Page<Reservation> reservations = reservationRepository.findAllWithFilters(date, time, status, pageable);

        List<AdminReservationDto> dtoList = new ArrayList<>();
        for (Reservation reservation : reservations.getContent()) {
            dtoList.add(new AdminReservationDto(reservation));
        }

        return new PageImpl<>(
                dtoList, pageable, reservations.getTotalElements()
        );
    }
}