package com.back.mozu.domain.reservation.controller;

import com.back.mozu.domain.customer.entity.Customer;
import com.back.mozu.domain.reservation.dto.ReservationDto;
import com.back.mozu.domain.reservation.service.ReservationService;
import com.back.mozu.global.response.Rq;
import com.back.mozu.global.response.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/my/reservations")
public class ApiV1ReservationController {

    private final ReservationService reservationService;
    private final Rq rq;

    @GetMapping("/")
    public RsData<List<ReservationDto.Response>> getMyReservations(
            @RequestHeader(value = "X-USER-ID", required = false) String xUserId
    ) {
        UUID customerId = resolveId(xUserId);
        if (customerId == null) {
            return RsData.of("401", "로그인이 필요한 서비스입니다.", null);
        }

        List<ReservationDto.Response> myReservations = reservationService.getMyReservation(customerId);
        return new RsData<>("예약 목록 조회에 성공했습니다.", "200", myReservations);
    }

    @PatchMapping("/{reservationId}")
    public RsData<ReservationDto.Response> modifyMyReservation(
            @PathVariable UUID reservationId,
            @Valid @RequestBody ReservationDto.Request request,
            @RequestHeader(value = "X-USER-ID", required = false) String xUserId) { // 👈 헤더 추가

        UUID customerId = resolveId(xUserId);
        if (customerId == null) {
            return RsData.of("401", "로그인이 필요한 서비스입니다.", null);
        }

        ReservationDto.Response modifiedReservation =
                reservationService.modifyMyReservation(reservationId, customerId, request);

        return new RsData<>("예약 수정에 성공했습니다.", "200", modifiedReservation);
    }

    @PostMapping("/{reservationId}/cancel")
    public RsData<ReservationDto.Response> cancelMyReservation(
            @PathVariable UUID reservationId,
            @RequestBody ReservationDto.CancelRequest request,
            @RequestHeader(value = "X-USER-ID", required = false) String xUserId) {

        UUID customerId = resolveId(xUserId);
        if (customerId == null) {
            return RsData.of("401", "로그인이 필요한 서비스입니다.", null);
        }

        ReservationDto.Response cancelledReservation =
                reservationService.cancelMyReservation(customerId, reservationId, request.cancelReason());

        return new RsData<>("예약 취소에 성공했습니다.", "200", cancelledReservation);
    }

    private UUID resolveId(String xUserId) {
        if (xUserId != null && !xUserId.isEmpty()) {
            return UUID.fromString(xUserId);
        }

        Customer actor = rq.getActor();
        return (actor != null) ? actor.getId() : null;
    }
}