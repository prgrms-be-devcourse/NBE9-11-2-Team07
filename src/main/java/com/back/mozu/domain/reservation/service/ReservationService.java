package com.back.mozu.domain.reservation.service;

import com.back.mozu.domain.reservation.dto.ReservationDto;
import com.back.mozu.domain.reservation.entity.Reservation;
import com.back.mozu.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TimeSlotRepository timeSlotRepository;

    // 내 예약 정보 받아오기 - GET "/api/v1/my/reservations"
    public List<ReservationDto.Response> getMyReservation(UUID customerId) {

        // 고객의 모든 reservation을 리스트 형태로 받아오기
        List<Reservation> reservations = reservationRepository.findAllByCustomerId(customerId);

        // DTO에 담아서 리스트로 반환하기
        return reservations.stream()
                .map(ReservationDto.Response::from)
                .toList();
    }

    // 내 예약 정보 수정하기 - PATCH "/api/v1/my/reservations/{reservationId}"
    public ReservationDto.Response modifyMyReservation(UUID customerId, UUID reservationId, ReservationDto.Request request) {

        // 해당 예약 찾아오기
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ServiceException("예약을 찾을 수 없습니다."));

        // 권한 체크: 이 예약의 주인(DB)이 현재 요청자(Token)와 같은지 체크
        if (!reservation.getCustomer().getId().equals(customerId)) {
            throw new ServiceException("해당 예약을 수정할 권한이 없습니다.");
        }

        // 수정 된 예약의 시간대와 guestCount를 받아와서 변수로 선언
        TimeSlot newTimeSlot = timeSlotRepository.findByDateAndTime(request.date(), request.time())
                .orElseThrow(() -> new ServiceException("해당 시간대는 존재하지 않습니다."));

        int newGuestCount = request.guestCount();

        // 수정 데이터들 Reservation 엔티티에 있는 수정 메서드에 넣어주기
        reservation.modifyReservation(newTimeSlot, newGuestCount);

        // 업데이트 된 예약 사항 DTO로 반환
        return ReservationDto.Response.from(reservation);

        // TO DO: 이전 예약 status 업데이트 && 새 예약 status 업데이트
        // TO DO: 동시성 확인
    }


    // 내 예약 취소하기 - POST "/api/v1/my/reservations/{reservationId}/cancel"
    public ReservationDto.Response cancelMyReservation(UUID customerId, UUID reservationId) {

        // 해당 예약 찾아오기
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ServiceException("예약을 찾을 수 없습니다."));

        // 권한 체크: 이 예약의 주인(DB)이 현재 요청자(Token)와 같은지 체크
        if (!reservation.getCustomer().getId().equals(customerId)) {
            throw new ServiceException("해당 예약을 취소할 권한이 없습니다.");
        }

        reservation.cancelReservation();

        return ReservationDto.Response.from(reservation);
    }


}
