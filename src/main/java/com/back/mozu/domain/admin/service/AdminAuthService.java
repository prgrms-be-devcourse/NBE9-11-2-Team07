package com.back.mozu.domain.admin.service;

import com.back.mozu.domain.admin.dto.AdminLoginRequestDto;
import com.back.mozu.domain.admin.dto.AdminLoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    // TODO: 팀원 JwtUtil 완성되면 주입
    // private final JwtUtil jwtUtil;

    // TODO: 팀원 AdminRepository 완성되면 주입
    // private final AdminRepository adminRepository;

    public AdminLoginResponseDto login(AdminLoginRequestDto request) {
        // TODO: 1. DB에서 관리자 조회 (팀원 AdminRepository 완성되면 연결)
        // Admin admin = adminRepository.findByLoginId(request.getLoginId())
        //     .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다."));

        // TODO: 2. 비밀번호 검증 (팀원 passwordEncoder 완성되면 연결)
        // if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
        //     throw new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다.");
        // }

        // TODO: 3. JWT 발급 (팀원 JwtUtil 완성되면 연결)
        // String token = jwtUtil.generateToken(admin);

        return AdminLoginResponseDto.builder()
                .accessToken("TODO: JWT 토큰")
                .adminUser(AdminLoginResponseDto.AdminUserDto.builder()
                        .adminId(null)
                        .loginId(request.getLoginId())
                        .name("TODO: 관리자 이름")
                        .build())
                .build();
    }
}
