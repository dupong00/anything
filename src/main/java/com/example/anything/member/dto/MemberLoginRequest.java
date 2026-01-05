package com.example.anything.member.dto;

import com.example.anything.member.internal.domain.ProviderType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "로그인 요청")
public record MemberLoginRequest(
        @Schema(description = "고유 식별 값", example = "아이디 or 소셜 고유 ID")
        @NotBlank(message = "식별값은 필수입니다.")
        String identifier,

        @NotNull(message = "로그인 타입은 필수입니다.")
        ProviderType providerType,

        @Schema(description = "비밀번호", example = "tjdgns123")
        String password
) { }
