package com.back.mozu.domain.admin.dto;

import com.back.mozu.domain.customer.entity.Customer;
import com.back.mozu.domain.reservation.entity.Reservation;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class AdminReservationDto {

    private String reservationId;
    private String userId;
    private String userName;
    private String userEmail;
    private LocalDate date;
    private LocalTime time;
    private int guestCount;
    private String status;
    private LocalDateTime createdAt;

    public AdminReservationDto(Reservation reservation, Customer customer) {
        this.reservationId = reservation.getId().toString(); // UUID → String
        // TODO: N+1 문제 발생 가능 - 현재 userId만 저장되어 있어 Customer 정보 별도 조회 필요
        //       추후 Reservation 엔티티에 Customer JOIN 방식으로 개선 필요
        this.userId = reservation.getUserId().toString();
        this.userName = customer.getName();   // Customer 엔티티에서
        this.userEmail = customer.getEmail();// UUID → String
        this.date = reservation.getTimeSlot().getDate();
        this.time = reservation.getTimeSlot().getTime();
        this.guestCount = reservation.getGuestCount();
        this.status = reservation.getStatus().name();        // Enum → String
        this.createdAt = reservation.getCreatedAt();
    }
}