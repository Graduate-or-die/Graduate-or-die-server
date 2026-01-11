package server.pome.experience.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import server.pome.experience.dto.request.SaveExperienceRequest;
import server.pome.experience.dto.request.UpdateExperienceRequest;
import server.pome.experience.dto.response.SaveUpdateExperienceResponse;
import server.pome.experience.service.ExperienceService;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/experiences")
@Tag(name = "Experience", description = "포트폴리오_경력 API")
public class ExperienceController {
    private final ExperienceService experienceService;

    @Operation(summary = "경력 저장")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @PostMapping
    public ResponseEntity<BaseResponse<SaveUpdateExperienceResponse>> saveExperience(Authentication authentication, @Valid @RequestBody SaveExperienceRequest request) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        SaveUpdateExperienceResponse result = experienceService.saveExperience(userId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "경력 수정")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @Parameter(name = "blockId", description = "경력 ID", required = true)
    @PatchMapping
    public ResponseEntity<BaseResponse<SaveUpdateExperienceResponse>> updateExperience(Authentication authentication, @RequestParam("blockId") Long blockId, @Valid @RequestBody UpdateExperienceRequest request) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        SaveUpdateExperienceResponse result = experienceService.updateExperience(userId, blockId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
