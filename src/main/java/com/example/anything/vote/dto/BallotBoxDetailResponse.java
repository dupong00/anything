package com.example.anything.vote.dto;

import com.example.anything.vote.Status;
import com.example.anything.vote.internal.domain.BallotBox;
import com.example.anything.vote.internal.domain.VoteOption;
import java.time.LocalDateTime;
import java.util.List;

public record BallotBoxDetailResponse (
        Long id,
        String title,
        LocalDateTime createAt,
        LocalDateTime deadline,
        Status status,
        String locationName,
        List<OptionDto> options
){
    public static BallotBoxDetailResponse from(BallotBox ballotBox) {
        int total = ballotBox.getVoteOptions().stream()
                .mapToInt(VoteOption::getCount)
                .sum();

        return new BallotBoxDetailResponse(
            ballotBox.getId(),
            ballotBox.getTitle(),
            ballotBox.getCreateAt(),
            ballotBox.getDeadline(),
            ballotBox.getStatus(),
            ballotBox.getLocationName(),
            ballotBox.getVoteOptions().stream()
                .map(option -> OptionDto.from(option, total))
                .toList()
        );
    }
}

