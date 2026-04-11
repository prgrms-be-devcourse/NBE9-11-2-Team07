package com.back.storymate.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberProfileDtoRq(
        @NotBlank(message = "닉네임은 비어있을 수 없습니다.")
        @Size(min = 2, max = 20, message = "닉네임은 x~y자 사이여야 합니다.") // 정의 필요
        String username,
        String bio
) {}