package com.back.mozu.domain.admin.controller;

import com.back.mozu.domain.admin.dto.AdminLoginRequestDto;
import com.back.mozu.domain.admin.dto.AdminLoginResponseDto;
import com.back.mozu.domain.admin.service.AdminAuthService;
import com.back.mozu.global.redis.RedisUtil;
import com.back.mozu.global.response.RsData;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import com.back.mozu.global.config.JwtProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;


@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final RedisUtil redisUtil;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<RsData<AdminLoginResponseDto>> login(
            @RequestBody AdminLoginRequestDto request,
            HttpServletResponse response) {  // 추가

        AdminLoginResponseDto loginResponse = adminAuthService.login(request);

        // Refresh Token 쿠키로 전달
        Cookie refreshCookie = new Cookie("refreshToken", loginResponse.getRefrestToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);  // 7일
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(new RsData<>("로그인에 성공했습니다.", "200", loginResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<RsData<Void>> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {  // 추가

        // Redis에서 Refresh Token 삭제
        if (refreshToken != null) {
            String userId = jwtProvider.getUserId(refreshToken);
            redisUtil.delete("refresh:" + userId);
        }

        // 쿠키 삭제
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // 즉시 만료
        response.addCookie(cookie);

        return ResponseEntity.ok(new RsData<>("로그아웃 되었습니다.", "200", null));
    }
    @PostMapping("/refresh")
    public ResponseEntity<RsData<String>> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null) {
            return ResponseEntity.status(401)
                    .body(new RsData<>("refreshToken이 없습니다.", "401", null));
        }

        // 토큰 유효성 검증
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(401)
                    .body(new RsData<>("유효하지 않은 refreshToken입니다.", "401", null));
        }

        // userId 꺼내기
        String userId = jwtProvider.getUserId(refreshToken);

        // Redis에서 저장된 토큰과 비교
        String savedToken = redisUtil.get("refresh:" + userId);
        if (!refreshToken.equals(savedToken)) {
            return ResponseEntity.status(401)
                    .body(new RsData<>("만료된 refreshToken입니다.", "401", null));
        }

        // 새 Access Token 발급
        String role = jwtProvider.getRole(refreshToken);
        String newAccessToken = jwtProvider.createToken(userId, role);

        return ResponseEntity.ok(new RsData<>("토큰이 재발급되었습니다.", "200", newAccessToken));
    }
}