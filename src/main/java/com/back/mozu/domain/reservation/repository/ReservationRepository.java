package com.back.mozu.domain.reservation.repository;

import com.back.mozu.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    // 특정 고객의 예약 하나를 찾을 때 (상세 조회용)
    Optional<Reservation> findReservationByCustomerId(UUID customerId);

    // 특정 고객의 모든 예약 목록을 가져올 때
    List<Reservation> findAllByCustomerId(UUID customerId);
}