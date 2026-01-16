package com.example.anything.vote.internal.controller;

import com.example.anything.common.ApiResponse;
import com.example.anything.vote.Status;
import com.example.anything.vote.dto.BallotBoxDetailResponse;
import com.example.anything.vote.dto.BallotBoxRequest;
import com.example.anything.vote.dto.BallotBoxesResponse;
import com.example.anything.vote.dto.VoteRequest;
import com.example.anything.vote.internal.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Vote", description = "투표 관련 API")
@RestController
@RequiredArgsConstructor
public class VoteController {
    private final VoteService voteService;

    @PostMapping("/member/ballot-boxes")
    @Operation(summary = "투표 생성", description = "투표 생성")
    public ResponseEntity<?> createBallotBox(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BallotBoxRequest ballotBoxRequest
    ){
        Long userId = Long.parseLong(userDetails.getUsername());

        Long ballotBoxId = voteService.createBallotBox(ballotBoxRequest, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ballotBoxId));
    }

    @PostMapping("/member/vote/cast")
    @Operation(summary = "투표 하기", description = "투표 기능")
    public ResponseEntity<?> castVote(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody VoteRequest voteRequest
            ){
        Long userId = Long.parseLong(userDetails.getUsername());

        Long ballotBoxId = voteService.castVote(userId, voteRequest.ballotBoxId(), voteRequest.menuList());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ballotBoxId));
    }

    @DeleteMapping("/member/ballot-boxes/{id}")
    @Operation(summary = "투표함 삭제 하기")
    public ResponseEntity<?> deleteBallotBox(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long ballotBoxId
    ){
        Long userId = Long.parseLong(userDetails.getUsername());

        voteService.deleteBallotBox(userId, ballotBoxId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }

    @GetMapping("/member/ballot-boxes")
    @Operation(summary = "전체 투표함 가져오기")
    public ResponseEntity<?> getBallotBoxes(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "status", required = false) Status status
            ){
        Long userId = Long.parseLong(userDetails.getUsername());

        List<BallotBoxesResponse> ballotBoxes = voteService.getBallotBoxes(userId, status);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(ballotBoxes));
    }

    @GetMapping("/member/ballot-boxes/{id}")
    @Operation(summary = "특정 투표함 상세 조회")
    public ResponseEntity<?> getBallotBox(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long ballotBoxId
    ){
        Long userId = Long.parseLong(userDetails.getUsername());

        BallotBoxDetailResponse ballotBox = voteService.getBallotBox(userId, ballotBoxId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(ballotBox));
    }
}
