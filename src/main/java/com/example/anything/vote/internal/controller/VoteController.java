package com.example.anything.vote.internal.controller;

import com.example.anything.common.ApiResponse;
import com.example.anything.vote.dto.BallotBoxRequest;
import com.example.anything.vote.internal.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
