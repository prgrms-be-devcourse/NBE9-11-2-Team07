package com.back.mozu.domain.customer.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String provider;

    @Column(name = "provider_id", nullable = false, unique = true, length = 255)
    private String providerId;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(length = 255)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Customer(String email, String provider, String providerId, String role, String password) {
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }
}