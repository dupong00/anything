package com.example.anything.vote.internal.domain;

import com.example.anything.common.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum VoteErrorCode implements ErrorCode {
    BALLOT_BOX_NOT_FOUND(HttpStatus.NOT_FOUND, "V001", "존재하지 않는 투표함입니다."),
    MENU_NOT_FOUND(HttpStatus.BAD_REQUEST, "V002", "존재하지 않는 메뉴가 포함되어 있습니다."),
    VOTE_ALREADY_CLOSED(HttpStatus.BAD_REQUEST, "V003", "이미 마감된 투표입니다."),
    ALREADY_VOTED(HttpStatus.CONFLICT, "V004", "이미 이 투표에 참여하셨습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
