package com.back.mozu.domain.admin.dto;

import com.back.mozu.domain.reservation.entity.Reservation;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class AdminReservationDto {

    private String reservationId;
    private String userName;
    private String userEmail;
    private LocalDate date;
    private LocalTime time;
    private int guestCount;
    private String status;
    private LocalDateTime createdAt;

    public AdminReservationDto(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.userName = reservation.getCustomer().getEmail();
        this.userEmail = reservation.getCustomer().getEmail();
        this.date = reservation.getTimeSlot().getDate();
        this.time = reservation.getTimeSlot().getTime();
        this.guestCount = reservation.getGuestCount();
        this.status = reservation.getStatus();
        this.createdAt = reservation.getCreatedAt();
    }
}