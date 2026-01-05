package com.example.anything.member.dto;

import com.example.anything.common.jwt.JwtToken;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답")
public record MemberLoginResponse (
        @Schema(description = "액세스 토큰", example = "asdif39zdnvz9x3hv90z")
        String accessToken,

        @Schema(description = "토큰 종류", example = "Bearer")
        String tokenType,

        @Schema(description = "액세스 토큰 만료 시간", example = "3600")
        Long expiresIn
    ) {

    public static MemberLoginResponse create(JwtToken token){
        return new MemberLoginResponse(
                token.getAccessToken(),
                token.getGrantType(),
                token.getExpiresIn()
        );
    }

}
