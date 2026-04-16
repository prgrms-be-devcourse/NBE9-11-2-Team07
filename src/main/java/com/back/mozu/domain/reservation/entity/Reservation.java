package com.back.mozu.domain.reservation.entity;


import com.back.mozu.domain.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA용 기본 생성자 (외부에서 new Reservation() 직접 호출 불가)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID id;

    // LAZY: 즉시 로딩 X, customer 정보가 필요할 때만 조회 (성능 최적화)
    // EAGER로 하면 예약 조회 시 무조건 유저도 같이 조회 → N+1 문제 발생 가능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_slot_id", nullable = false)
    private TimeSlot timeSlot;

    @Column(name = "guest_count", nullable = false)
    private int guestCount;

    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 생성자에 @Builder: createdAt은 자동으로 현재 시간 설정
    // 클래스에 @Builder 붙이면 매번 .createdAt(LocalDateTime.now()) 써줘야 함
    @Builder
    public Reservation(Customer customer, TimeSlot timeSlot, int guestCount, String status) {
        this.customer = customer;
        this.timeSlot = timeSlot;
        this.guestCount = guestCount;
        this.status = status;
        this.createdAt = LocalDateTime.now(); // 자동으로 현재 시간 저장
    }
}
