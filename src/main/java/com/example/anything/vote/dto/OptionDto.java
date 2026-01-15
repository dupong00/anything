package com.example.anything.vote.dto;

import com.example.anything.vote.internal.domain.VoteOption;
import lombok.Builder;
import lombok.Getter;

@Builder
public record OptionDto (
    Long menuId,
    String menuName,
    Integer count,
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