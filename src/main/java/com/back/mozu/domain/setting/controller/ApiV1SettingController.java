package com.back.mozu.domain.setting.controller;

import com.back.mozu.domain.setting.dto.HolidayDto;
import com.back.mozu.domain.setting.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class ApiV1SettingController {

    private final HolidayService holidayService;

    @GetMapping("/holidays")
    public HolidayDto.GetHolidaysResponse getHolidays(
            @RequestParam(required = false) String month
    ) {
        return holidayService.getHolidays(month);
    }

    @PostMapping("/holidays")
    public HolidayDto.CreateHolidayResponse createHoliday(
            @Valid @RequestBody HolidayDto.CreateHolidayRequest request
    ) {
        return holidayService.createHoliday(request);
    }
}

