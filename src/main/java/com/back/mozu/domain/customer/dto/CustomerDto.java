package com.back.mozu.domain.customer.dto;

import java.util.UUID;

public class CustomerDto {

    public record MeResponse(
            UUID userId,
            String name,
            String email,
            String role
    ) {
    }
}
