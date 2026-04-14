package com.back.mozu.domain.admin.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class AdminDto {

    public record CancelReservationRequest(
            String reason
    ) {
    }

    public record CancelReservationResponse(
            UUID reservationId,
            String status,
            String reason,
            LocalDateTime canceledAt
    ) {
    }
}