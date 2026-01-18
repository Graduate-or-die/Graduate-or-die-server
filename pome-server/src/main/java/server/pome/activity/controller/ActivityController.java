package server.pome.activity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import server.pome.activity.dto.request.SaveActivityRequest;
import server.pome.activity.dto.request.UpdateActivityRequest;
import server.pome.activity.dto.response.SaveUpdateActivityResponse;
import server.pome.activity.service.ActivityService;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/activities")
@Tag(name = "Activity", description = "포트폴리오_대내외활동 API")
public class ActivityController {

    private final ActivityService activityService;

    @Operation(summary = "대내외활동 저장")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @PostMapping
    public ResponseEntity<BaseResponse<SaveUpdateActivityResponse>> saveActivity(Authentication authentication, @Valid @RequestBody SaveActivityRequest request) {

        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        SaveUpdateActivityResponse result = activityService.saveActivity(userId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "대내외활동 수정")
    @Parameter(name = "blockId", description = "대내외활동 ID", required = true)
    @PatchMapping
    public ResponseEntity<BaseResponse<SaveUpdateActivityResponse>> updateActivity(Authentication authentication, @RequestParam("blockId") Long blockId, @Valid @RequestBody UpdateActivityRequest request) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        SaveUpdateActivityResponse result = activityService.updateActivity(userId, blockId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
