package com.example.anything.group.internal;

import com.example.anything.group.dto.GroupRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class Controller {
    @Tag(name = "Group API", description = "그룹 생성 및 관리를 위한 API")
    @RestController
    @RequestMapping("/api/member")
    public class GroupController {

        @Operation(summary = "새로운 그룹 생성", description = "그룹명과 설명을 입력받아 그룹을 생성합니다.")
        @PostMapping
        public ResponseEntity<String> createGroup(@RequestBody GroupRequest request) {
            // ... 로직
            return ResponseEntity.ok("그룹 생성 완료");
        }
    }
}
