package com.example.anything.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;

@Schema(description = "공통 API 응답 규격")
public record ApiResponse<T>(
        @Schema(description = "성공 여부", example = "true")
        boolean success,

        @Schema(description = "에러 코드 (성공 시 null)", example = "M001")
        @Nullable String code,

        @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
        String message,

        @Schema(description = "응답 데이터 (없는 경우 null)")
        @Nullable T data
) {

    public static <T> ApiResponse<T> success(@Nullable T data) {
        return new ApiResponse<>(true, null, "요청이 성공적으로 처리되었습니다.", data);
    }

    // 실패 응답
    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }
}