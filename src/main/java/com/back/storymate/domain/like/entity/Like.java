package com.back.storymate.domain.like.entity;

import com.back.storymate.domain.member.entity.Member;
import com.back.storymate.domain.world.entity.World;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
// 복잡 유니크 (composite unique) 규칙 적용
@Table(name = "likes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_world", columnNames = {"member_id", "world_id"})
})
@Builder
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "world_id")
    private World world;

    @CreatedDate
    private LocalDateTime createdAt;

}
