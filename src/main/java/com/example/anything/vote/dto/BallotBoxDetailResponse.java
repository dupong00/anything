package com.example.anything.vote.dto;

import com.example.anything.vote.Status;
import com.example.anything.vote.internal.domain.BallotBox;
import com.example.anything.vote.internal.domain.VoteOption;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Schema(description = "투표함 상세 조회")
public record BallotBoxDetailResponse (
        @Schema(description = "투표함 ID", example = "1")
        Long id,

        @Schema(description = "투표 제목", example = "오늘 점심 메뉴 투표")
        String title,

        @Schema(description = "생성 일시")
        LocalDateTime createAt,

        @Schema(description = "마감 일시")
        LocalDateTime deadline,

        @Schema(description = "투표함 상태", example = "ACTIVE = 현재 1등 표기, CLOSED = 최종 결과 표기", implementation = Status.class)
        Status status,

        @Schema(description = "위치 명칭", example = "동두천 집")
        String locationName,

        @Schema(description = "투표 선택지 및 실시간 득표 현황 리스트")
        List<OptionDto> options,

        @Size(max = 3)
        @Schema(description = "1위 메뉴 리스트, 동표 가능", example = "[1, 3]")
        List<Long> winners
){
    public static BallotBoxDetailResponse from(BallotBox ballotBox) {
        int total = ballotBox.getVoteOptions().stream()
                .mapToInt(opt -> opt.getCount() != null ? opt.getCount() : 0)
                .sum();

        List<Long> winners = ballotBox.getWinnerMenuIds();

        if (ballotBox.getStatus() == Status.ACTIVE){
            winners = calculateCurrentLeaders(ballotBox);
        }

        return new BallotBoxDetailResponse(
                ballotBox.getId(),
                ballotBox.getTitle(),
                ballotBox.getCreateAt(),
                ballotBox.getDeadline(),
                ballotBox.getStatus(),
                ballotBox.getLocationName(),
                ballotBox.getVoteOptions().stream()
                    .map(option -> OptionDto.from(option, total))
                    .toList(),
                winners
        );
    }

    private static List<Long> calculateCurrentLeaders(BallotBox ballotBox){
        int maxCount = ballotBox.getVoteOptions().stream()
                .mapToInt(opt -> opt.getCount() != null ? opt.getCount() : 0)
                .max()
                .orElse(0);

        if (maxCount == 0) return List.of();

        return ballotBox.getVoteOptions().stream()
                .filter(opt -> opt.getCount() != null && opt.getCount() == maxCount)
                .map(VoteOption::getMenuId)
                .toList();
    }
}

