package com.back.mozu.domain.admin.controller;

import com.back.mozu.domain.admin.dto.AdminLoginRequestDto;
import com.back.mozu.domain.admin.dto.AdminLoginResponseDto;
import com.back.mozu.domain.admin.service.AdminAuthService;
import com.back.mozu.global.response.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<RsData<AdminLoginResponseDto>> login(
            @RequestBody AdminLoginRequestDto request) {
        AdminLoginResponseDto response = adminAuthService.login(request);
        return ResponseEntity.ok(new RsData<>("로그인에 성공했습니다.", "200", response));
    }
}