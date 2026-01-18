package com.example.anything.group.internal.domain;

import com.example.anything.common.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GroupErrorCode implements ErrorCode {
    NOT_GROUP_OWNER(HttpStatus.BAD_REQUEST,"G001", "그룹 관리자가 아닙니다."),
    GROUP_NOT_FOUND(HttpStatus.BAD_REQUEST,"G002", "그룹이 존재하지 않습니다."),
    NOT_GROUP_MEMBER(HttpStatus.BAD_REQUEST,"G003", "해당 그룹에 멤버가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
