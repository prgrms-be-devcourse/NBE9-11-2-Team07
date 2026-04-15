package com.back.mozu.domain.admin.controller;


import com.back.mozu.domain.admin.dto.AdminLoginRequestDto;
import com.back.mozu.domain.admin.dto.AdminLoginResponseDto;
import com.back.mozu.global.response.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {
    private final AmdinAuthService adminAuthService;

    @PostMapping("/login")
    public RsData<AdminLoginResponseDto> login(@Valid @RequestBody AdminLoginRequestDto request){
        AdminLoginRequestDto response = adminAuthService.login(request);
        return new RsData<>("로그인에 성공했습니다.","200",response);

    }
}
