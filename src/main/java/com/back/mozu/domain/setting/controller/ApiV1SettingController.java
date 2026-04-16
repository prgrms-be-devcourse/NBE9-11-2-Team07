package com.back.mozu.domain.setting.controller;

import com.back.mozu.domain.setting.dto.SettingDto;
import com.back.mozu.domain.setting.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.back.mozu.global.response.RsData;
import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class ApiV1SettingController {

    private final SettingService settingService;

    @GetMapping("/settings")
    public ResponseEntity<RsData<SettingDto.GetSettingResponse>> getSetting() {
        SettingDto.GetSettingResponse response = settingService.getSetting();

        return ResponseEntity.ok(
                RsData.of("200", "설정 조회에 성공했습니다.", response)
        );
    }
}
