package com.back.storymate.domain.member.service;

import com.back.storymate.domain.member.dto.MemberProfileDtoRq;
import com.back.storymate.domain.member.dto.MemberProfileDtoRs;
import com.back.storymate.domain.member.entity.MemberProfile;
import com.back.storymate.domain.member.repository.MemberProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberProfileRepository memberProfileRepository;

    public MemberProfileDtoRs getProfile(String username) {

        MemberProfile memberProfile = memberProfileRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        // 임시 값. 추가 함수 필요
        int worldCount = 5;
        Long totalInteractions = 128L;

        return MemberProfileDtoRs.of(memberProfile, worldCount, totalInteractions);
    }

    public MemberProfileDtoRs updateProfile(UUID id, MemberProfileDtoRq request) {

        // 수정할 멤버 객체 찾아오기
        MemberProfile memberProfile = memberProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        // 유저명 중복 체크 exception
        if (!memberProfile.getUsername().equals(request.username())) {
            if (memberProfileRepository.existsByUsername(request.username())) {
                throw new RuntimeException("이미 사용 중인 유저명입니다.");
            }
        }

        // 엔티티 메서드 사용해서 수정
        memberProfile.updateProfile(request.username(), request.bio());

        // 임시 값. 추가 함수 필요
        int worldCount = 5;
        Long totalInteractions = 128L;

        return MemberProfileDtoRs.of(memberProfile, worldCount, totalInteractions);
    }



}
