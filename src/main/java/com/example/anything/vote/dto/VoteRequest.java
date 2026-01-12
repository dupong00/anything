package com.example.anything.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "투표 하기")
public record VoteRequest (
        @NotNull
        @Schema(description = "투표함 아이디", example = "1")
        Long ballotBoxId,

        @NotNull
        @Size(min = 1, max = 3, message = "1개 이상 3개 이하의 메뉴를 선택해야 합니다")
        @Schema(description = "투표 메뉴 리스트", example = "[1, 2, 4]")
        List<Long> menuList
){}