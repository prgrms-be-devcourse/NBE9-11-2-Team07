package com.back.storymate.domain.like.service;

import com.back.storymate.domain.like.dto.LikeDto;
import com.back.storymate.domain.like.entity.Like;
import com.back.storymate.domain.like.repository.LikeRepository;
import com.back.storymate.domain.member.entity.Member;
import com.back.storymate.domain.world.entity.World;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;

    @Transactional
    public LikeDto.LikeRs toggleLike(Member member, World world) {

        // 해당 유저가 해당 월드에 좋아요를 했는지 객체 존재 여부로 확인
        Optional<Like> like = likeRepository.findByMemberAndWorld(member, world);

        boolean isLiked = false;

        // 객체가 없다면 객체 생성 후 저장
        if (like.isEmpty()) {
            Like newLike = Like.builder()
                    .member(member)
                    .world(world)
                    .build();

            likeRepository.save(newLike);
            isLiked = true;
        // 객체가 있다면 지울 것
        } else {
            likeRepository.delete(like.get());
            isLiked = false;
        }

        // 해당 world의 현재 좋아요 숫자 계산
        int totalLikesCount = likeRepository.countByWorldId(world.getId());

        return LikeDto.LikeRs.of(isLiked, totalLikesCount);
    }
}
