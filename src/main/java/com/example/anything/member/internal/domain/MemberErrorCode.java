package com.example.anything.member.internal.domain;

import com.example.anything.common.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "연결된 계쩡을 찾을 수 없습니다."),
    ALREADY_LINKED_ACCOUNT(HttpStatus.BAD_REQUEST, "M002", "이미 해당 소셜 계정으로 연결되어 있습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED,"M003", "비밀번호가 일치하지 않습니다."),
    UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST, "M004", "지원하지 않는 로그인 방식입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
