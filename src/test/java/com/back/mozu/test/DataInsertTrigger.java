package com.back.mozu.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev") // 도커 MySQL 설정이 있는 프로필 선택
@Import(MozuDataSeeder.class) // 작성하신 시더 클래스를 강제로 불러옴
public class DataInsertTrigger {

    @Test
    void triggerSeeding() {
        // 이 메서드 옆의 재생 버튼(▶️)을 누르면 시딩이 시작됨
        System.out.println(">>> 시딩 프로세스가 시작되었습니다. 콘솔 로그를 확인하세요.");
    }
}