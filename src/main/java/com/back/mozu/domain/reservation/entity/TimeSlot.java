package com.back.mozu.domain.reservation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 타임슬롯 관리
@Entity
@Table(name = "time_slots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TimeSlot {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private LocalDate date;
    private LocalTime time;
    private int stock;

    @Version // 낙관적 락 관리 필드
    private int version;

    public void decreaseStock(int count) {
        if (this.stock < count) {
            throw new IllegalArgumentException("해당 시간대의 예약이 불가능합니다.");
        }

        this.stock -= count;
    }
}