package com.back.mozu.domain.reservation.repository;

import com.back.mozu.domain.reservation.entity.Reservation;
import com.back.mozu.domain.reservation.entity.ReservationStatus;
import com.back.mozu.domain.reservation.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// 예약 CRUD 작업
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    boolean existsByUserIdAndTimeSlotAndStatusNot(UUID customerId, TimeSlot timeSlot, ReservationStatus status);

    List<Reservation> findAllByCustomerId(UUID customerId);
}