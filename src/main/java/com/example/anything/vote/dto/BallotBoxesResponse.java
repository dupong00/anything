package com.example.anything.vote.dto;

import com.example.anything.vote.Status;
import com.example.anything.vote.internal.domain.BallotBox;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "투표함 전체 조회")
public record BallotBoxesResponse(
    @Schema(description = "투표함 ID", example = "1")
    Long id,

    @Schema(description = "투표 제목", example = "오늘 점심 메뉴 투표")
    String title,

    @Schema(description = "생성 일시")
    LocalDateTime createAt,

    @Schema(description = "마감 일시")
    LocalDateTime deadline,

    @Schema(description = "투표함 상태", implementation = Status.class)
    Status status,

    @Schema(description = "위치 명칭", example = "동두천 집")
    String locationName
) {
    public static BallotBoxesResponse from(BallotBox ballotBox) {
        return new BallotBoxesResponse(
                ballotBox.getId(),
                ballotBox.getTitle(),
                ballotBox.getCreateAt(),
                ballotBox.getDeadline(),
                ballotBox.getStatus(),
                ballotBox.getLocationName()
        );
    }
}
