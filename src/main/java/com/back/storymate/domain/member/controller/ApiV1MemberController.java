package com.back.storymate.domain.member.controller;

import com.back.storymate.domain.member.dto.MemberProfileDtoRq;
import com.back.storymate.domain.member.dto.MemberProfileDtoRs;
import com.back.storymate.domain.member.service.MemberService;
import com.back.storymate.global.response.Rq;
import com.back.storymate.global.response.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ApiV1MemberController {

    private final MemberService memberService;
    private final Rq rq;

    @GetMapping("/{username}")
    public RsData<MemberProfileDtoRs> getProfile(@PathVariable String username) {

        MemberProfileDtoRs memberProfile = memberService.getProfile(username);

        return new RsData(
                "프로필 조회에 성공했습니다.",
                "200",
                memberProfile
        );
    }

    @PatchMapping("/profile")
    public RsData<MemberProfileDtoRs> updateProfile(@Valid @RequestBody MemberProfileDtoRq memberProfile) {

        // Rq 통해 현재 요청 유저의 UUID 가져오기
        // Rq 클래스 완성되면 확인 후 컨펌 필요
        UUID id = rq.getId();

        // 서비스 editProfile 메서드에 매개변수 주입
        MemberProfileDtoRs updatedMemberProfile = memberService.updateProfile(id, memberProfile)

        return new RsData<>(
                "프로필이 수정되었습니다.",
                "200",
                updatedMemberProfile
        );

    }


}
