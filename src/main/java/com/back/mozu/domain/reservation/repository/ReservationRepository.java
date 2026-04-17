package com.back.mozu.domain.reservation.repository;

import com.back.mozu.domain.reservation.entity.TimeSlot;
import com.back.mozu.domain.reservation.entity.Reservation;
import com.back.mozu.domain.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Optional<Reservation> findByUserIdAndStatus(UUID customerId, ReservationStatus status);
    boolean existsByUserIdAndTimeSlotAndStatusNot(UUID customerId, TimeSlot timeSlot, ReservationStatus status);
    List<Reservation> findAllByCustomerId(UUID customerId);
}
