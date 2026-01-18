package server.pome.award.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import server.pome.award.dto.request.SaveAwardRequest;
import server.pome.award.dto.request.UpdateAwardRequest;
import server.pome.award.dto.response.SaveUpdateAwardResponse;
import server.pome.award.service.AwardService;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/awards")
@Tag(name = "Award", description = "포트폴리오_수상경력 API")
public class AwardController {

    private final AwardService awardService;

    @Operation(summary = "수상경력 저장")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<SaveUpdateAwardResponse>> saveAward(Authentication authentication, @Valid @RequestBody SaveAwardRequest request, @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {

        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        SaveUpdateAwardResponse result = awardService.saveAward(userId, request, files);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "수상경력 수정")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @Parameter(name = "blockId", description = "수상경력 ID", required = true)
    @PatchMapping("/{userId}")
    public ResponseEntity<BaseResponse<SaveUpdateAwardResponse>> updateAward(@PathVariable Long userId, @RequestParam("blockId") Long blockId, @Valid @RequestBody UpdateAwardRequest request) {
        SaveUpdateAwardResponse result = awardService.updateAward(userId, blockId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
