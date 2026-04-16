package com.back.mozu.domain.customer.controller;


import com.back.mozu.global.response.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
