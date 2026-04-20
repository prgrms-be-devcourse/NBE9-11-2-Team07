package com.back.mozu.domain.customer.controller;

import com.back.mozu.domain.customer.dto.CustomerDto;
import com.back.mozu.domain.customer.entity.Customer;
import com.back.mozu.domain.customer.service.AuthService;
import com.back.mozu.global.response.Rq;
import com.back.mozu.global.response.RsData;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ApiV1AuthController {

    private final AuthService authService;
    private final Rq rq;

    @GetMapping("/google")
    public void googleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/api/v1/auth/oauth2/authorization/google");
    }

    @GetMapping("/me")
    public ResponseEntity<RsData<CustomerDto.MeResponse>> getMe() {
        Customer actor = rq.getActor();
        if (actor == null) {
            return ResponseEntity
                    .status(401)
                    .body(RsData.of("401", "인증된 유저 정보가 없습니다.", null));
        }
        CustomerDto.MeResponse response = new CustomerDto.MeResponse(actor);
        return ResponseEntity.ok(
                RsData.of("200", "유저 정보 조회에 성공했습니다.", response)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<RsData<Void>> logout() {
        return ResponseEntity.ok(new RsData<>("로그아웃 되었습니다.", "200", null));
    }
}