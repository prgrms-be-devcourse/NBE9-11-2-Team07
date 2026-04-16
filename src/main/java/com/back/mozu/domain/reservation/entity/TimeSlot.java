package com.back.mozu.domain.reservation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "time_slots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @Column(name = "current_stock", nullable = false)
    private int currentStock;

    @Version
    @Column(nullable = false)
    private int version;

    @Builder
    public TimeSlot(LocalDate date, LocalTime time, int currentStock) {
        this.date = date;
        this.time = time;
        this.currentStock = currentStock;
        this.version = 0;
    }
}
