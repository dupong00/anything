package com.example.anything.recommend.internal.controller;

import com.example.anything.common.ApiResponse;
import com.example.anything.recommend.dto.RecommendResultResponse;
import com.example.anything.recommend.internal.domain.RestaurantMenu;
import com.example.anything.recommend.internal.service.RecommendService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendService recommendService;

    @GetMapping("/member/ballot-boxes/{id}/result")
    public ResponseEntity<?> getRecommendations(
            @PathVariable("id") Long ballotBoxId
    ) {
        List<RestaurantMenu> results = recommendService.createRecommend(ballotBoxId);

        List<RecommendResultResponse> response = results.stream()
                .map(RecommendResultResponse::from)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }
}