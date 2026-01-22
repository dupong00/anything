package com.example.anything.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "투표 생성")
public record BallotBoxRequest (
        @Schema(description = "그룹 아이디", example = "122")
        Long groupId,

        @Schema(description = "위도", example = "135.51")
        double latitude,

        @Schema(description = "경도", example = "125.24")
        double longitude,

        @Schema(description = "위치 이름", example = "동두천 집 앞")
        String locationName,

        @Schema(description = "투표 이름", example = "아무나 먹고 싶은거 투표 ㄱㄱ")
        String title,

        @NotEmpty(message = "최소 하나 이상의 카테고리를 선택해야 합니다.")
        @Valid
        @Schema(description = "선택된 메뉴 카테고리 목록",
                example = "[{\"categoryId\": 1, \"menuIds\": [1, 2, 3]}]")
        List<CategorySelection> selections,

        @Schema(description = "투표 마감 시간", example = "2026-01-07T18:30:00", type = "string")
        @Future(message = "마감 시간은 현재 시간 이후여야 합니다.")
        LocalDateTime deadline
        ){}
