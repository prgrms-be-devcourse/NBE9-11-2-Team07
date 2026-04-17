package com.back.mozu.domain.reservation.repository;

import com.back.mozu.domain.reservation.entity.TimeSlot;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

// 타임슬롯 CRUD 작업
public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {
}