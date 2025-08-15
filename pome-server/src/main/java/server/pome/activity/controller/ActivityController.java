package server.pome.activity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.pome.activity.dto.request.SaveActivityRequest;
import server.pome.activity.dto.response.SaveActivityResponse;
import server.pome.activity.service.ActivityService;
import server.pome.education.dto.request.SaveEducationRequest;
import server.pome.education.dto.response.SaveEducationResponse;
import server.pome.global.domain.BaseResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/activities")
@Tag(name = "Activity", description = "포트폴리오_대내외활동 API")
public class ActivityController {

    private final ActivityService activityService;

    @Operation(summary = "대내외활동 저장")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @PostMapping("/{userId}")
    public ResponseEntity<BaseResponse<SaveActivityResponse>> saveActivity(@PathVariable Long userId, @Valid @RequestBody SaveActivityRequest request) {

        SaveActivityResponse result = activityService.saveActivity(userId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
