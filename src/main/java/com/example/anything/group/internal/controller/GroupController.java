package com.example.anything.group.internal.controller;

import com.example.anything.common.ApiResponse;
import com.example.anything.group.internal.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Group", description = "그룹 관련 API")
@RestController
@RequiredArgsConstructor
public class GroupController {
    private  final GroupService groupService;

    @Operation(summary = "그룹생성", description = "그룹 생성")
    @PostMapping("/public/group")
    public ResponseEntity<?> groupCreate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String name
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());

        Long groupId = groupService.createGroup(name, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(groupId));
    }

    @Operation(summary = "그룹삭제", description = "그룹 삭제")
    @DeleteMapping("/public/group")
    public ResponseEntity<?> groupDelete(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long groupId
    ){
        Long userId = Long.parseLong(userDetails.getUsername());

        groupService.deleteGroup(groupId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }
}
