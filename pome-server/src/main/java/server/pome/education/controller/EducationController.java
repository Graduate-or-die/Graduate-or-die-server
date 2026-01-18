package server.pome.education.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import server.pome.education.dto.request.SaveEducationRequest;
import server.pome.education.dto.request.UpdateEducationRequest;
import server.pome.education.dto.response.SaveUpdateEducationResponse;
import server.pome.education.service.EducationService;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/educations")
@Tag(name = "Education", description = "포트폴리오_학력 API")
public class EducationController {

    private final EducationService educationService;

    @Operation(summary = "학력 저장")
    @PostMapping
    public ResponseEntity<BaseResponse<SaveUpdateEducationResponse>> saveEducation(Authentication authentication, @Valid @RequestBody SaveEducationRequest request) {

        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        SaveUpdateEducationResponse result = educationService.saveEducation(userId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "학력 수정")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @PatchMapping
    public ResponseEntity<BaseResponse<SaveUpdateEducationResponse>> updateEducation(Authentication authentication, @Valid @RequestBody UpdateEducationRequest request) {

        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        SaveUpdateEducationResponse result = educationService.updateEducation(userId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
