package com.back.mozu.domain.reservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// 사용자 ID
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserId {
    @Column(name = "user_id", nullable = false)
    private UUID value;

    public UserId(UUID value) {
        this.value = value;
    }
}