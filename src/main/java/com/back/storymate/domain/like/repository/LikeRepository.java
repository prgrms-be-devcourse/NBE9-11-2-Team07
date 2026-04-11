package com.back.storymate.domain.like.repository;

import com.back.storymate.domain.like.entity.Like;
import com.back.storymate.domain.member.entity.Member;
import com.back.storymate.domain.world.entity.World;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {

    // 특정 유저가 특정 월드에 좋아요 눌렀는지 확인
    boolean existsByMemberAndWorld(Member member, World world);

    // 좋아요 취소를 위해 특정 유저와 월드로 좋아요 엔티티 찾기
    Optional<Like> findByMemberAndWorld(Member member, World world);

    // 특정 월드의 총 좋아요 개수
    int countByWorldId(UUID worldId);
}