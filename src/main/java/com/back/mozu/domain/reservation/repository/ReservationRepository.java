package com.back.mozu.domain.reservation.repository;

import com.back.mozu.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, String>, ReservationRepositoryCustom {
}
