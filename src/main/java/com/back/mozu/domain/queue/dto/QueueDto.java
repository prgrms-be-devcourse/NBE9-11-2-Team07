package com.back.mozu.domain.queue.dto;

import com.back.mozu.domain.reservation.entity.ReservationStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class QueueDto {

    // 예약 시도 요청 데이터
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttemptRequest {
        private UUID timeSlotId;
        private int guestCount;
    }

    // 예약 대기열 데이터
    @Getter
    @AllArgsConstructor
    public static class AttemptResponse {
        private UUID attemptId;
    }

    // 예약 처리 상태 반환 데이터
    @Getter
    @AllArgsConstructor
    public static class StatusResponse {
        private ReservationStatus status;
    }
}