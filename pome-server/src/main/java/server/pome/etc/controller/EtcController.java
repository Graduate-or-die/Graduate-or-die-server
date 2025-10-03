package server.pome.etc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.pome.etc.dto.request.SaveEtcRequest;
import server.pome.etc.dto.request.UpdateEtcRequest;
import server.pome.etc.dto.response.SaveUpdateEtcResponse;
import server.pome.etc.service.EtcService;
import server.pome.global.domain.BaseResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/etc")
@Tag(name = "Etc", description = "포트폴리오_기타 API")
public class EtcController {

    private final EtcService etcService;

    @Operation(summary = "기타 저장")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @PostMapping("/{userId}")
    public ResponseEntity<BaseResponse<SaveUpdateEtcResponse>> saveEtc(@PathVariable Long userId, @Valid @RequestBody SaveEtcRequest request) {

        SaveUpdateEtcResponse result = etcService.saveEtc(userId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "기타 수정")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @PatchMapping("/{userId}")
    public ResponseEntity<BaseResponse<SaveUpdateEtcResponse>> updateEtc(@PathVariable Long userId, @Valid @RequestBody UpdateEtcRequest request) {

        SaveUpdateEtcResponse result = etcService.updateEtcResponse(userId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
