package com.back.mozu.domain.admin.controller;

import com.back.mozu.domain.admin.dto.AdminDto;
import com.back.mozu.domain.admin.service.AdminService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class ApiV1AdminController {

    private final AdminService adminService;

    @PostMapping("/reservations/{reservationId}/cancel")
    public AdminDto.CancelReservationResponse cancelReservation(
            @PathVariable UUID reservationId,
            @Valid @RequestBody AdminDto.CancelReservationRequest request
    ) {
        return adminService.cancelReservation(reservationId, request);
    }
}

