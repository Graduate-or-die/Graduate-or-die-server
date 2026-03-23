package server.pome.mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;
import server.pome.mate.dto.response.GetMateRequestResponse;
import server.pome.mate.service.MateQueryService;
import server.pome.mate.service.MateService;
import server.pome.portfolio.dto.response.PreviewMateResponse;
import server.pome.portfolio.dto.response.PreviewResponse;
import server.pome.user.dto.response.GetUserResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mates")
@Tag(name = "Mate", description = "메이트 API")
public class MateController {

    private final MateService mateService;
    private final MateQueryService mateQueryService;

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
    @DeleteMapping
    public ResponseEntity<BaseResponse<String>> unmatchMate(
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        return ResponseEntity.ok(BaseResponse.success(mateService.unmatchMate(userId)));
    }

    @Operation(summary = "메이트 프로필 조회")
    @GetMapping("/profile")
    public ResponseEntity<BaseResponse<GetUserResponse>> getMateProfile(
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        return ResponseEntity.ok(BaseResponse.success(mateQueryService.getMateProfile(userId)));
    }

    @Operation(summary = "메이트 포트폴리오 조회")
    @Parameter(name = "typeId", description = "항목 ID")
    @GetMapping("/portfolio")
    public ResponseEntity<BaseResponse<Object>> getMatePortfolio(
            Authentication authentication,
            @RequestParam(required = false) Long typeId
    ) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        Object result = mateQueryService.getMatePortfolio(userId, typeId);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "메이트 포트폴리오 공개범위 여부 리스트 조회")
    @Parameter(name = "limit", description = "미리보기 개수 (최대 3개)", required = false)
    @GetMapping("/visibility")
    public ResponseEntity<BaseResponse<PreviewMateResponse>> getVisibilityAndPreview(
            Authentication authentication,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) List<Long> typeIds
    ) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        PreviewMateResponse response = mateQueryService.getVisibilityAndPreview(userId, limit, typeIds);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @Operation(summary = "메이트 존재 여부 확인 ")
    @GetMapping("/exists")
    public ResponseEntity<BaseResponse<Boolean>> hasMate(
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        boolean result = mateService.hasMate(userId);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
