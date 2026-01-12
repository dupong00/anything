package com.example.anything.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "투표 하기")
public record VoteRequest (
        @Schema(description = "투표함 아이디", example = "1")
        Long BallotBoxId,

        @Schema(description = "투표 메뉴 리스트", example = "[1, 2, 4]")
        List<Long> menuList
){}