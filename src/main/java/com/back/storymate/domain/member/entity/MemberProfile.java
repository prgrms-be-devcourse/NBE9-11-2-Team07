package com.back.storymate.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "member_profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class MemberProfile {

    @Id
    private UUID id; // Member의 ID와 동일한 값을 가짐

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Member의 PK를 이 엔티티의 PK로 매핑 (PK이자 FK 설정)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @LastModifiedDate // 수정 시 자동으로 시간 업데이트
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public MemberProfile(Member member, String username, String bio) {
        this.member = member;
        this.username = username;
        this.bio = bio;
    }

    // username 혹은 bio 하나만 수정하는 시나리오도 반영
    public void updateProfile(String username, String bio) {
        if (username != null && !username.isBlank()) {
            this.username = username;
        }

        if (bio != null) {
            this.bio = bio;
        }
    }
}
