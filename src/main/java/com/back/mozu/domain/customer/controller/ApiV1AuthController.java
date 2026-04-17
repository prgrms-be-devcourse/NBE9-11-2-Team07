package com.back.mozu.domain.customer.controller;

import com.back.mozu.domain.customer.dto.CustomerDto;
import com.back.mozu.domain.customer.service.AuthController;
import com.back.mozu.global.response.Rq;
import com.back.mozu.global.response.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class ApiV1AuthController {

    private final AuthController authController;
    private final Rq rq;

    @GetMapping("/me")
    public ResponseEntity<RsData<CustomerDto.MeResponse>> getMe() {
        String email = rq.getCurrentUserEmail();
        CustomerDto.MeResponse response = authController.getMe(email);

        return ResponseEntity.ok(
                RsData.of("200", "유저 정보 조회에 성공했습니다.", response)
        );
    }
}
