package com.back.storymate.domain.member.dto;

import com.back.storymate.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.UUID;

public record MemberDtoRs(
        UUID id,
        String email,
        String providerId,
        LocalDateTime createdAt
) {
    public MemberDtoRs(Member member) {
        this(
                member.getId(),
                member.getEmail(),
                member.getProviderId(),
                member.getCreatedAt()
        );
    }
}
