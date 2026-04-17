package com.back.mozu.domain.reservation.repository;

import com.back.mozu.domain.reservation.entity.TimeSlot;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

// 타임슬롯 CRUD 작업
public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {
    <T> ScopedValue<T> findByDateAndTime(@NotNull(message = "예약 날짜를 선택해주세요.") LocalDate date, @NotNull(message = "예약 시간을 선택해주세요.") LocalTime time);
}