package com.example.anything.vote.dto;

import com.example.anything.vote.internal.domain.VoteOption;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "투표 선택지(메뉴) 정보")
public record OptionDto (
    @Schema(description = "메뉴 ID", example = "101")
    Long menuId,

    @Schema(description = "메뉴 이름", example = "김치찌개")
    String menuName,

    @Schema(description = "득표수", example = "5")
    Integer count,

    @Schema(description = "득표율", example = "33.3")
    Double percentage

){
    public static OptionDto from(VoteOption option, int total) {
        return new OptionDto(
            option.getMenuId(),
            option.getMenuName(),
            option.getCount(),
            calculatePercentage(option.getCount(), total)
        );
    }

    private static double calculatePercentage(Integer count, int total){
        if (total == 0 || count == null){
            return 0.0;
        }
        return Math.round((double) count / total * 1000) / 10.0;
    }
}