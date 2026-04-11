package com.back.storymate.domain.member.repository;

import com.back.storymate.domain.member.entity.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, UUID> {
    Optional<MemberProfile> findByUsername(String username);
    boolean existsByUsername(String username);
}
