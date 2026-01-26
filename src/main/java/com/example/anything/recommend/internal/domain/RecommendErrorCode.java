package com.example.anything.recommend.internal.domain;

import com.example.anything.common.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RecommendErrorCode implements ErrorCode {
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "주소 정보를 찾을 수 없습니다."),
    RETRY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "R002", "식당 정보를 처리하는 중 일시적인 충돌이 발생했습니다. 잠시 후 다시 시도해주세요.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
