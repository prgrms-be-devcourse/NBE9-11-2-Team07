package com.back.mozu.domain.reservation.service;

import com.back.mozu.domain.reservation.dto.ReservationDto;
import com.back.mozu.domain.reservation.entity.Reservation;
import com.back.mozu.domain.reservation.entity.ReservationStatus;
import com.back.mozu.domain.reservation.entity.TimeSlot;
import com.back.mozu.domain.reservation.repository.ReservationRepository;
import com.back.mozu.domain.reservation.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.back.mozu.domain.customer.entity.Customer;
import com.back.mozu.domain.customer.service.CustomerService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ReleaseScheduler releaseScheduler;
    private final CustomerService customerService;

    // 내 예약 정보 받아오기 - GET "/api/v1/my/reservations"
    public List<ReservationDto.Response> getMyReservation(UUID customerId) {

        // 고객의 모든 reservation을 리스트 형태로 받아오기
        List<Reservation> reservations = reservationRepository.findAllByUserId(customerId);

        // DTO에 담아서 리스트로 반환하기
        return reservations.stream()
                .map(ReservationDto.Response::from)
                .toList();
    }

    // 내 예약 정보 수정하기 - PATCH "/api/v1/my/reservations/{reservationId}"
    @Transactional
    public ReservationDto.Response modifyMyReservation(UUID reservationId, UUID customerId, ReservationDto.Request request) {

        // 해당 예약 찾아오기
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ServiceException("예약을 찾을 수 없습니다."));

        // 권한 체크: 이 예약의 주인(DB)이 현재 요청자(Token)와 같은지 체크
        if (!reservation.getUserId().equals(customerId)) {
            throw new ServiceException("해당 예약을 수정할 권한이 없습니다.");
        }

        // 새로운 타임슬롯 조회
        // TO DO: 해당 DB에서 락 걸어줬는지 확인 필요
        TimeSlot newTimeSlot = (TimeSlot) timeSlotRepository.findByDateAndTime(request.date(), request.time())
                .orElseThrow(() -> new ServiceException("해당 시간대는 존재하지 않습니다."));

        // 새 타임슬롯 재고 확인 및 차감 (Occupy)
        // 새 예약 인원만큼 자리가 있는지 확인하고 차감
        // TO DO: guestCount가 재고보다 작은지 확인하는 로직 필요
        newTimeSlot.occupy(request.guestCount());

        // 기존 타임슬롯 재고 복구 (Release)
        // 수정 전 예약되어 있던 인원만큼 현재 타임슬롯의 재고를 다시 늘려줌
        // occupy로 예약 선점 완료 한 뒤에 release
        reservation.getTimeSlot().release(reservation.getGuestCount());

        // 수정 데이터들 Reservation 엔티티에 있는 수정 메서드에 넣어주기
        reservation.modifyReservation(newTimeSlot, request.guestCount());

        // 업데이트 된 예약 사항 DTO로 반환
        return ReservationDto.Response.from(reservation);
    }

    // 내 예약 취소하기 - POST "/api/v1/my/reservations/{reservationId}/cancel"
    @Transactional
    public ReservationDto.Response cancelMyReservation(UUID customerId, UUID reservationId, String cancelReason) {

        // 해당 예약 찾아오기
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ServiceException("예약을 찾을 수 없습니다."));

        // 권한 체크: 이 예약의 주인(DB)이 현재 요청자(Token)와 같은지 체크
        if (!reservation.getUserId().equals(customerId)) {
            throw new ServiceException("해당 예약을 취소할 권한이 없습니다.");
        }

        // 이미 취소된 예약인지 체크
        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new IllegalArgumentException("이미 취소된 예약입니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reservationDateTime = LocalDateTime.of(
                reservation.getTimeSlot().getDate(),
                reservation.getTimeSlot().getTime()
        );

        boolean isWithin24Hours = reservationDateTime.isBefore(now.plusHours(24));

        if (isWithin24Hours) {
            Customer customer = customerService.findById(customerId)
                    .orElseThrow(() -> new ServiceException("사용자를 찾을 수 없습니다."));
            customer.applyPenaltyUntil(now.plusMonths(3));
            cancelReason = "LATE_CANCEL";
        }

        boolean isRandomRelease = false;

        // [케이스 1] 오픈 직후 취소: 예약 오픈 후 30분 이내 취소 → 암표 의심 → 랜덤 반환 적용
        if (reservation.getReservationOpenedAt() != null) {
            if (LocalDateTime.now().isBefore(reservation.getReservationOpenedAt().plusMinutes(30))) {
                isRandomRelease = true;
            }
        }

        // [케이스 2] 반복 취소 유저: 같은 유저가 3개월 내 3번 이상 취소 → 어뷰징 의심 → 랜덤 반환 적용
        int cancelCount = reservationRepository
                .countByUserIdAndStatusAndCancelledAtAfter(
                        customerId,
                        ReservationStatus.CANCELED,
                        LocalDateTime.now().minusMonths(3)
                );

        if (cancelCount >= 3) {
            isRandomRelease = true;
        }

        if (isRandomRelease) {
            // 랜덤 반환: 10~60분 사이 랜덤 시간 후 재고 반환
            int randomMinutes = 10 + new Random().nextInt(51);
            LocalDateTime releaseAt = LocalDateTime.now().plusMinutes(randomMinutes);
            reservation.pendingCancel(cancelReason, releaseAt);
            // 스케줄러에 반환 예약 등록
            releaseScheduler.schedule(reservation);
        } else {
            // 즉시 반환: 재고 즉시 복구 후 예약 취소 처리
            reservation.getTimeSlot().release(reservation.getGuestCount());
            reservation.cancelReservation(cancelReason);
        }

        return ReservationDto.Response.from(reservation);
    }
}