package com.back.storymate.domain.like.dto;

import lombok.Builder;

public class LikeDto {

    public record LikeRq(
            // 요청 URL에 이미 worldId가 포함 되어있으므로 빈 객체도 괜찮음
    ) {}

    @Builder
    public record LikeRs(
            boolean isLiked,
            int totalLikesCount
    ) {
        public static LikeRs of(boolean isLiked, int totalLikesCount) {
            return LikeRs.builder()
                    .isLiked(isLiked)
                    .totalLikesCount(totalLikesCount)
                    .build();
        }

    }
}
