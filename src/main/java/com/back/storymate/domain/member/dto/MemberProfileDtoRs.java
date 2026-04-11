package com.back.storymate.domain.member.dto;

import com.back.storymate.domain.member.entity.MemberProfile;

import java.time.LocalDateTime;
import java.util.UUID;

public record MemberProfileDtoRs(
        UUID id,
        String username,
        String bio,
        int worldCount,
        Long totalInteractions,
        LocalDateTime updatedAt
) {
    public static MemberProfileDtoRs of(MemberProfile memberProfile, int worldCount, Long totalInteractions) {
        return new MemberProfileDtoRs(
                memberProfile.getId(),
                memberProfile.getUsername(),
                memberProfile.getBio(),
                worldCount,
                totalInteractions,
                memberProfile.getUpdatedAt()
        );
    }
}