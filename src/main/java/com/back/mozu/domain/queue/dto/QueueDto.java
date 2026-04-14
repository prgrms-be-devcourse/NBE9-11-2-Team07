package com.back.mozu.domain.queue.dto;

import com.back.mozu.domain.reservation.entity.ReservationStatus;
import java.util.UUID;

public class QueueDto {

    public static class AttemptRequest {
        private UUID timeSlotId;
        private int guestCount;

        protected AttemptRequest() {
        }

        public UUID getTimeSlotId() {
            return timeSlotId;
        }

        public int getGuestCount() {
            return guestCount;
        }
    }

    public static class AttemptResponse {
        private UUID attemptId;

        public AttemptResponse(UUID attemptId) {
            this.attemptId = attemptId;
        }

        public UUID getAttemptId() {
            return attemptId;
        }
    }

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