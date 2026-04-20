package com.back.mozu.domain.reservation.repository;

import com.back.mozu.domain.reservation.entity.Reservation;
import com.back.mozu.domain.reservation.entity.ReservationStatus;
import com.back.mozu.domain.reservation.entity.TimeSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Optional<Reservation> findByUserIdAndStatus(UUID customerId, ReservationStatus status);
    boolean existsByUserIdAndTimeSlotAndStatusNot(UUID customerId, TimeSlot timeSlot, ReservationStatus status);
    List<Reservation> findAllByUserId(UUID userId);
    List<Reservation> findAllByStatusAndReleaseAtBefore(ReservationStatus status, LocalDateTime releaseAt);
    List<Reservation> findAllByStatus(ReservationStatus status);
    int countByTimeSlotDate(LocalDate date);

    // JPA 네이밍으로 못 만드는 복잡한 쿼리라 @Query 필요
    @Query("SELECT r FROM Reservation r " +
            "WHERE (:date IS NULL OR r.timeSlot.date = :date) " +
            "AND (:time IS NULL OR r.timeSlot.time = :time) " +
            "AND (:status IS NULL OR r.status = :status)")
    Page<Reservation> findAllWithFilters(
            @Param("date") LocalDate date,
            @Param("time") LocalTime time,
            @Param("status") ReservationStatus status,
            Pageable pageable);

    int countByUserIdAndStatusAndCancelledAtAfter(UUID customerId, ReservationStatus reservationStatus, LocalDateTime localDateTime);
}