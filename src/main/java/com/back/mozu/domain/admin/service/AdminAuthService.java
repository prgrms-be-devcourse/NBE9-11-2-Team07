package com.back.mozu.domain.admin.service;

import com.back.mozu.domain.admin.dto.AdminLoginRequestDto;
import com.back.mozu.domain.admin.dto.AdminLoginResponseDto;
import com.back.mozu.domain.customer.entity.Customer;
import com.back.mozu.domain.customer.repository.CustomerRepository;
import com.back.mozu.global.config.JwtProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;



    public AdminLoginResponseDto login(AdminLoginRequestDto request) {
        // email로 관리자 찾기 (loginId = email)
        Customer customer = customerRepository.findByEmail(request.getLoginId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 계정입니다"));

        // ADMIN role 확인
        if (!customer.getRole().equals("ADMIN")) {
            throw new RuntimeException("관리자 권한이 없습니다");
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다");
        }

        // JWT 발급
        String token = jwtProvider.createToken(customer.getId(), customer.getRole());

        return AdminLoginResponseDto.builder()
                .accessToken(token)
                .adminUser(AdminLoginResponseDto.AdminUserDto.builder()
                        .adminId(customer.getId())
                        .loginId(customer.getEmail())
                        .name(customer.getEmail()) // name 필드 없으면 email로 대체
                        .build())
                .build();
    }
}