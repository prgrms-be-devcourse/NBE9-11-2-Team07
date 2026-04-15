package com.back.mozu.domain.queue.controller;

import com.back.mozu.domain.queue.dto.QueueDto.AttemptRequest;
import com.back.mozu.domain.queue.dto.QueueDto.AttemptResponse;
import com.back.mozu.domain.queue.service.QueueService;
import com.back.mozu.global.response.RsData;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 외부 API 요청 처리 컨트롤러
@RestController
@RequestMapping("/api/v1/reservations/attempts")
@RequiredArgsConstructor
public class ApiV1QueueController {

    private final QueueService queueService;

    // 예약 시도 생성 및 대기열 진입
    @PostMapping
    public RsData<AttemptResponse> createAttempt(@RequestBody AttemptRequest request) {
        UUID customerId = UUID.randomUUID(); // 테스트용 임시 UUID
        AttemptResponse response = queueService.enqueueAttempt(customerId, request);
        return RsData.of("202", "대기열 진입 성공", response);
    }
}