package com.back.mozu.domain.customer.controller;

import com.back.mozu.domain.customer.dto.CustomerDto;
import com.back.mozu.domain.customer.service.AuthController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class ApiV1AuthController {

    private final AuthController authController;

    @GetMapping("/me")
    public CustomerDto.MeResponse getMe(
            @RequestHeader("X-USER-EMAIL") String email
    ) {
        return authController.getMe(email);
    }
}
