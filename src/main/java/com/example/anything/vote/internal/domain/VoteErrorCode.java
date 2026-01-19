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
    ALREADY_VOTED(HttpStatus.CONFLICT, "V004", "이미 이 투표에 참여하셨습니다."),
    VOTE_OPTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "V005", "해당 투표함에서 선택 불가능한 메뉴입니다."),
    INVALID_VOTE_COUNT(HttpStatus.BAD_REQUEST, "V006", "최소 한개 이상 투표해야 합니다."),
    EXCEED_MAX_COUNT(HttpStatus.BAD_REQUEST, "V007", "투표 개수는 최대 3개까지 선택 가능합니다."),
    BALLOT_BOX_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "V008", "현재 투표가 활성화 되어 있지 않습니다."),
    DUPLICATE_MENU_SELECTION(HttpStatus.BAD_REQUEST,"V009", "중복된 항목은 투표가 불가능합니다."),
    BALLOT_BOX_NOT_AUTHORITY(HttpStatus.BAD_REQUEST,"V010", "투표함을 삭제할 권한이 없습니다."),
    ALREADY_DELETED(HttpStatus.CONFLICT, "V011", "이미 삭제된 투표함입니다."),
    INVALID_DEADLINE(HttpStatus.BAD_REQUEST, "V012", "마감 기한이 비어있습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
