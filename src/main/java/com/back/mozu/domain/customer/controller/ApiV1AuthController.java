package com.back.mozu.domain.customer.controller;


import com.back.mozu.domain.customer.dto.CustomerDto;
import com.back.mozu.domain.customer.entity.Customer;
import com.back.mozu.domain.customer.service.AuthService;
import com.back.mozu.global.response.Rq;
import com.back.mozu.global.response.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ApiV1AuthController {

    private final AuthService authService;
    private final Rq rq;

    @GetMapping("/me")
    public ResponseEntity<RsData<CustomerDto.MeResponse>> getMe() {

        // 1. Rq를 통해 보안 검증이 끝난 유저(Actor)를 바로 가져옴
        Customer actor = rq.getActor();

        // 2. 로그인 여부 체크 (인증되지 않은 유저 방어)
        if (actor == null) {
            return ResponseEntity
                    .status(401)
                    .body(RsData.of("401", "인증된 유저 정보가 없습니다.", null));
        }

        // 3. 서비스에 엔티티를 통째로 넘기거나, 여기서 바로 DTO로 변환
        CustomerDto.MeResponse response = new CustomerDto.MeResponse(actor);

        return ResponseEntity.ok(
                RsData.of("200", "유저 정보 조회에 성공했습니다.", response)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<RsData<Void>> logout() {
        return ResponseEntity.ok(new RsData<>("로그아웃 되었습니다." , "200",null));
    }
}
