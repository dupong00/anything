package com.example.anything.member.dto;

import com.example.anything.member.internal.domain.ProviderType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "회원가입 요청")
public record MemberSignUpRequest(
    @Schema(description = "이메일", example = "tjdgns9954@gmail.com")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String  email,

    @Schema(description = "닉네임", example = "나성훈")
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    String nickname,

    @NotNull(message = "로그인 타입은 필수입니다.")
    ProviderType providerType,

    @Schema(description = "고유 식별 값", example = "아이디 or 소셜 고유 ID")
    @NotBlank(message = "식별값은 필수입니다.")
    String identifier,

    @Schema(description = "전화번호", example = "010-1234-5678")
    @NotBlank(message = "전화번호는 필수입니다.")
    String phoneNumber,

    @Schema(description = "비밀번호", example = "tjdgns123")
    String password


) {
    public void validate(){
        if (providerType == ProviderType.LOCAL){
            if(password == null || password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$")){
                throw new IllegalArgumentException("로컬 가입 시 비밀번호 정책을 준수해야 합니다.");
            }
        }
    }
}
