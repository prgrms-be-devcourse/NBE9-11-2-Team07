package com.back.mozu.domain.reservation.repository;

import com.back.mozu.domain.reservation.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {
    Optional<TimeSlot> findByDateAndTime(LocalDate date, LocalTime time);
}