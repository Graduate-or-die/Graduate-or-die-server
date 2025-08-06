package server.pome.experience.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.experience.dto.request.SaveExperienceRequest;
import server.pome.experience.dto.response.SaveExperienceResponse;
import server.pome.experience.service.ExperienceService;
import server.pome.global.domain.BaseResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/experiences")
@Tag(name = "Experience", description = "포트폴리오_경력 API")
public class ExperienceController {
    private final ExperienceService experienceService;

    @Operation(summary = "학력 저장")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @PostMapping("/{userId}")
    public ResponseEntity<BaseResponse<SaveExperienceResponse>> saveExperience(@PathVariable Long userId, @Valid @RequestBody SaveExperienceRequest request) {
        SaveExperienceResponse result = experienceService.saveExperience(userId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
