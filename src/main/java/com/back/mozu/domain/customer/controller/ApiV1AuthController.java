package com.back.mozu.domain.customer.controller;


import com.back.mozu.global.response.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import com.back.mozu.domain.customer.dto.CustomerDto;
import com.back.mozu.domain.customer.service.AuthController;
import com.back.mozu.global.response.Rq;
import com.back.mozu.domain.customer.service.AuthService;
import com.back.mozu.global.response.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ApiV1AuthController {

    @PostMapping("/logout")
    public ResponseEntity<RsData<Void>> logout() {
        return ResponseEntity.ok(new RsData<>("로그아웃 되었습니다." , "200",null));
    }
}
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class ApiV1AuthController {

    private final AuthService authService;
    private final Rq rq;

    @GetMapping("/me")
    public ResponseEntity<RsData<CustomerDto.MeResponse>> getMe() {
        
        String email = rq.getCurrentUserEmail();
        
        CustomerDto.MeResponse response = authService.getMe(email);

        return ResponseEntity.ok(
                RsData.of("200", "유저 정보 조회에 성공했습니다.", response)
        );
    }
}
