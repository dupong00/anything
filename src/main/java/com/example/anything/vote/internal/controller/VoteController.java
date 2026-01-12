package com.example.anything.vote.internal.controller;

import com.example.anything.common.ApiResponse;
import com.example.anything.vote.dto.BallotBoxRequest;
import com.example.anything.vote.dto.VoteRequest;
import com.example.anything.vote.internal.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Vote", description = "투표 관련 API")
@RestController
@RequiredArgsConstructor
public class VoteController {
    private final VoteService voteService;

    @PostMapping("/member/vote/ballot-box")
    @Operation(summary = "투표 생성", description = "투표 생성")
    public ResponseEntity<?> createBallotBox(@Valid @RequestBody BallotBoxRequest ballotBoxRequest){
        Long ballotBoxId = voteService.createBallotBox(ballotBoxRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ballotBoxId));
    }

    @PostMapping("/member/vote/cast")
    @Operation(summary = "투표 하기", description = "투표 기능")
    public ResponseEntity<?> castVote(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody VoteRequest voteRequest
            ){
        Long userId = Long.parseLong(userDetails.getUsername());

        Long ballotBoxId = voteService.castVote(userId, voteRequest.BallotBoxId(), voteRequest.menuList());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ballotBoxId));
    }
}
