package com.back.mozu.domain.queue.dto;

import com.back.mozu.domain.reservation.entity.ReservationStatus;
import java.util.UUID;

public class QueueDto {

    // 예약 시도 요청 객체
    public static class AttemptRequest {
        private UUID timeSlotId; // 타임슬롯 고유 ID
        private int guestCount; // 방문 인원수

        // Json 기본 생성자
        protected AttemptRequest() {
        }

        // 데이터 전달을 위한 객체 (타임슬롯 고유 ID)
        public UUID getTimeSlotId() {
            return timeSlotId;
        }

        // 데이터 전달을 위한 객체 (방문 인원수)
        public int getGuestCount() {
            return guestCount;
        }
    }

    // 예약 시도 성공 시 응답 객체
    public static class AttemptResponse {
        private UUID attemptId;

        public AttemptResponse(UUID attemptId) {
            this.attemptId = attemptId;
        }

        public UUID getAttemptId() {
            return attemptId;
        }
    }

    // 현재 처리 상태 객체
    public static class StatusResponse {
        private ReservationStatus status;

        public StatusResponse(ReservationStatus status) {
            this.status = status;
        }

        public ReservationStatus getStatus() {
            return status;
        }
    }
}