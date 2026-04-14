package com.back.mozu.domain.setting.controller;

import com.back.mozu.domain.setting.dto.SettingDto;
import com.back.mozu.domain.setting.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class ApiV1SettingController {

    private final SettingService settingService;

    @GetMapping("/settings")
    public SettingDto.GetSettingResponse getSetting() {
        return settingService.getSetting();
    }
}
