package server.pome.mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;
import server.pome.mate.dto.response.GetMateRequestResponse;
import server.pome.mate.service.MateService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mates")
@Tag(name = "Mate", description = "메이트 API")
public class MateController {

  private final MateService mateService;

  @Operation(summary = "메이트 신청자 리스트 조회")
  @GetMapping("/requests")
  public ResponseEntity<BaseResponse<List<GetMateRequestResponse>>> getMateRequest(
          Authentication authentication
  ) {

    User user = (User) authentication.getPrincipal();
    Long userId = user.getId();
    List<GetMateRequestResponse> result = mateService.getMateRequest(userId);
    return ResponseEntity.ok(BaseResponse.success(result));
  }

  @Operation(summary = "메이트 신청")
  @Parameter(name = "mateId", description = "신청을 받는 유저 ID", required = true)
  @PostMapping("/requests/{mateId}")
  public ResponseEntity<BaseResponse<String>> requestMate(
      @PathVariable Long mateId,
      Authentication authentication
  ) {

    User user = (User) authentication.getPrincipal();
    Long userId = user.getId();
    return ResponseEntity.ok(BaseResponse.success(mateService.requestMate(mateId, userId)));
  }

  @Operation(summary = "메이트 수락|매칭")
  @Parameter(name = "mateId", description = "수락당하는 유저 ID", required = true)
  @PostMapping("/{mateId}")
  public ResponseEntity<BaseResponse<String>> matchMate(
      @PathVariable Long mateId,
      Authentication authentication
  ) {

    User user = (User) authentication.getPrincipal();
    Long userId = user.getId();
    return ResponseEntity.ok(BaseResponse.success(mateService.matchMate(mateId, userId)));
  }

  @Operation(summary = "메이트 거절")
  @Parameter(name = "mateId", description = "거절당하는 유저 ID", required = true)
  @PatchMapping("/requests/{mateId}")
  public ResponseEntity<BaseResponse<String>> rejectMate(
      @PathVariable Long mateId,
      Authentication authentication
  ) {

    User user = (User) authentication.getPrincipal();
    Long userId = user.getId();
    return ResponseEntity.ok(BaseResponse.success(mateService.rejectMate(mateId, userId)));
  }

  @Operation(summary = "메이트 해제")
  @Parameter(name = "mateId", description = "해제 대상 유저 ID", required = true)
  @PatchMapping("/{mateId}")
  public ResponseEntity<BaseResponse<String>> unmatchMate(
      @PathVariable Long mateId,
      Authentication authentication
  ) {

    User user = (User) authentication.getPrincipal();
    Long userId = user.getId();
    return ResponseEntity.ok(BaseResponse.success(mateService.unmatchMate(mateId, userId)));
  }
}
