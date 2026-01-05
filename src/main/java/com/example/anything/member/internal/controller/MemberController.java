package com.example.anything.member.internal.controller;

import com.example.anything.common.ApiResponse;
import com.example.anything.member.dto.MemberSignUpRequest;
import com.example.anything.member.internal.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "회원가입", description = "로컬 및 소셜 계정을 통합하여 회원가입")
    @PostMapping("/public/member/signup")
    public ResponseEntity<?> signup(@RequestBody MemberSignUpRequest request) {
        request.validate();

        memberService.signUp(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
    }
}
