package com.back.mozu.domain.reservation.entity;

import com.back.mozu.domain.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "reservations") // 테이블명은 복수형이 관례야
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "BINARY(16)")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_slot_id", columnDefinition = "BINARY(16)")
    private TimeSlot timeSlot;

    @Column(nullable = false)
    private int guestCount;

    @Column(nullable = false, length = 20)
    private String status; // PENDING, CONFIRMED, CANCELED

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public void modifyReservation(TimeSlot newTimeSlot, int guestCount) {
        this.timeSlot = newTimeSlot;
        this.guestCount = guestCount;
        this.status = "CONFIRMED";
    }

    public void cancelReservation() {
        this.status = "CANCELLED";
    }
}