package com.back.mozu.domain.queue.controller;

import com.back.mozu.domain.queue.dto.QueueDto.AttemptRequest;
import com.back.mozu.domain.queue.dto.QueueDto.AttemptResponse;
import com.back.mozu.domain.queue.dto.QueueDto.StatusResponse;
import com.back.mozu.domain.queue.service.QueueService;
import com.back.mozu.global.response.RsData;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.back.mozu.domain.customer.entity.Customer;
import com.back.mozu.global.response.Rq;

@RestController
@RequestMapping("/api/v1/reservations/attempts")
@RequiredArgsConstructor
public class ApiV1QueueController {

    private final QueueService queueService;
    private final Rq rq;

    @PostMapping
    public RsData<AttemptResponse> createAttempt(
            @RequestBody AttemptRequest request,
            @RequestHeader(value = "X-USER-ID", required = false) String xUserId
    ) {
        UUID customerId;

        if (xUserId != null && !xUserId.isEmpty()) {
            customerId = UUID.fromString(xUserId);
        } else {
            Customer actor = rq.getActor();
            if (actor == null) {
                return RsData.of("401", "로그인이 필요한 서비스입니다.", null);
            }
            customerId = actor.getId();
        }

        AttemptResponse response = queueService.enqueueAttempt(customerId, request);
        return RsData.of("202", "대기열 진입 성공", response);
    }

    @GetMapping("/{attemptId}")
    public RsData<StatusResponse> checkStatus(@PathVariable UUID attemptId) {
        StatusResponse response = queueService.getAttemptStatus(attemptId);
        return RsData.of("200", "상태 조회 성공", response);
    }
}