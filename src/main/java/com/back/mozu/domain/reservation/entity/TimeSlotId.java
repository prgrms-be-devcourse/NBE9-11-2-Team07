package com.back.mozu.domain.reservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// 타임슬롯 (시간대) ID
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeSlotId {
    @Column(name = "time_slot_id", nullable = false)
    private UUID value;

    public TimeSlotId(UUID value) {
        this.value = value;
    }
}