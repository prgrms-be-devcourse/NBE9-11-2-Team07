package com.back.mozu.domain.admin.controller;

import com.back.mozu.domain.admin.dto.AdminReservationDto;
import com.back.mozu.domain.admin.service.AdminService;
import com.back.mozu.global.response.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class ApiV1AdminController {

    private final AdminService adminService;

    @GetMapping("/reservations")
    public RsData<Page<AdminReservationDto>> getReservations(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalTime time,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<AdminReservationDto> result = adminService.getReservations(date, time, status, pageable);

        return new RsData<>("예약 현황 조회에 성공했습니다.", "200", result);
    }
}
