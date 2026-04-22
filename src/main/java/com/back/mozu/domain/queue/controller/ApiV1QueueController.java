package com.back.mozu.domain.queue.controller;

import com.back.mozu.domain.queue.dto.QueueDto.AttemptRequest;
import com.back.mozu.domain.queue.dto.QueueDto.AttemptResponse;
import com.back.mozu.domain.queue.dto.QueueDto.StatusResponse;
import com.back.mozu.domain.queue.service.QueueService;
import com.back.mozu.global.response.RsData;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservations/attempts")
@RequiredArgsConstructor
public class ApiV1QueueController {

    private final QueueService queueService;

    @PostMapping
    public ResponseEntity<RsData<AttemptResponse>> createAttempt(@RequestBody AttemptRequest request) {
        UUID customerId = UUID.fromString("9f4edea9-3d43-11f1-b715-0242ac150006");
        AttemptResponse response = queueService.enqueueAttempt(customerId, request);
        RsData<AttemptResponse> rsData = RsData.of("202", "대기열 진입 성공", response);
        return ResponseEntity.status(202).body(rsData);
    }

    @GetMapping("/{attemptId}")
    public RsData<StatusResponse> checkStatus(@PathVariable UUID attemptId) {
        StatusResponse response = queueService.getAttemptStatus(attemptId);
        return RsData.of("200", "상태 조회 성공", response);
    }
}