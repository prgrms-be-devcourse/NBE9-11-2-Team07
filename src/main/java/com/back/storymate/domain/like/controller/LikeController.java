package com.back.storymate.domain.like.controller;

import com.back.storymate.domain.like.dto.LikeDto;
import com.back.storymate.domain.like.service.LikeService;
import com.back.storymate.domain.member.entity.Member;
import com.back.storymate.domain.world.entity.World;
import com.back.storymate.domain.world.service.WorldService;
import com.back.storymate.global.response.Rq;
import com.back.storymate.global.response.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final WorldService worldService;
    private final Rq rq;

    @PostMapping("/api/v1/{world_id}/likes")
    public RsData<LikeDto.LikeRs> toggleLike(@PathVariable UUID worldId) {

        // 유저와 월드 가져오기
        Member member = rq.getMember();
        World world = worldService.findWorldById(worldId);

        // 서비스에 있는 toggleLike 메서드 호출
        LikeDto.LikeRs likeRs = likeService.toggleLike(member, world);

        return new RsData(
                "좋아요가 반영되었습니다.",
                "200",
                likeRs
        );

    }

}
