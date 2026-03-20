package server.pome.activity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.pome.activity.dto.request.SaveActivityRequest;
import server.pome.activity.dto.request.UpdateActivityRequest;
import server.pome.activity.dto.response.SaveUpdateActivityResponse;
import server.pome.activity.service.ActivityService;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/activities")
@Tag(name = "Activity", description = "포트폴리오 대내외활동 API")
public class ActivityController {

  private final ActivityService activityService;

  @Operation(summary = "대내외활동 저장")
  @PostMapping
  public ResponseEntity<BaseResponse<SaveUpdateActivityResponse>> saveActivity(
      Authentication authentication,
      @Valid @RequestBody SaveActivityRequest request
  ) {
    User user = (User) authentication.getPrincipal();
    SaveUpdateActivityResponse result = activityService.saveActivity(user.getId(), request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(BaseResponse.success(result));
  }

  @Operation(summary = "대내외활동 수정")
  @Parameter(name = "blockId", description = "대내외활동 블록 ID", required = true)
  @PatchMapping("/{blockId}")
  public ResponseEntity<BaseResponse<SaveUpdateActivityResponse>> updateActivity(
      Authentication authentication,
      @PathVariable("blockId") Long blockId,
      @Valid @RequestBody UpdateActivityRequest request
  ) {
    User user = (User) authentication.getPrincipal();
    SaveUpdateActivityResponse result = activityService.updateActivity(user.getId(), blockId, request);
    return ResponseEntity.ok(BaseResponse.success(result));
  }
}
