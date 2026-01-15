package com.example.anything.vote.dto;

import com.example.anything.vote.Status;
import com.example.anything.vote.internal.domain.BallotBox;
import java.time.LocalDateTime;

public record BallotBoxesResponse(
    Long id,
    String title,
    LocalDateTime createAt,
    LocalDateTime deadline,
    Status status,
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
