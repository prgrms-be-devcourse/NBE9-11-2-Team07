package com.back.mozu.domain.reservation.repository;

import com.back.mozu.domain.reservation.entity.Reservation;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

// 예약 CRUD 작업
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
}