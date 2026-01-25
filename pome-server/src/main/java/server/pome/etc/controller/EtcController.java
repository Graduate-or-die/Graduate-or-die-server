package server.pome.etc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import server.pome.etc.dto.request.SaveEtcRequest;
import server.pome.etc.dto.request.UpdateEtcRequest;
import server.pome.etc.dto.response.SaveUpdateEtcResponse;
import server.pome.etc.service.EtcService;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/etc")
@Tag(name = "Etc", description = "포트폴리오_기타 API")
public class EtcController {

    private final EtcService etcService;

    @Operation(summary = "기타 저장")
    @PostMapping
    public ResponseEntity<BaseResponse<SaveUpdateEtcResponse>> saveEtc(Authentication authentication, @Valid @RequestBody SaveEtcRequest request) {

        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        SaveUpdateEtcResponse result = etcService.saveEtc(userId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "기타 수정")
    @PatchMapping
    public ResponseEntity<BaseResponse<SaveUpdateEtcResponse>> updateEtc(Authentication authentication, @Valid @RequestBody UpdateEtcRequest request) {

        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        SaveUpdateEtcResponse result = etcService.updateEtcResponse(userId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
