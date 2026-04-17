package com.back.mozu.domain.customer.controller;

import com.back.mozu.domain.customer.dto.CustomerDto;
import com.back.mozu.domain.customer.service.AuthService;
import com.back.mozu.global.response.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class ApiV1AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<RsData<CustomerDto.MeResponse>> getMe(
            @RequestHeader("X-USER-EMAIL") String email
    ) {
        CustomerDto.MeResponse response = authService.getMe(email);

        return ResponseEntity.ok(
                RsData.of("200", "유저 정보 조회에 성공했습니다.", response)
        );
    }
}