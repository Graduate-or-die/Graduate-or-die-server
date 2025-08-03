package server.pome.mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.BaseResponse;
import server.pome.mate.dto.response.GetMateRequestResponse;
import server.pome.mate.service.MateService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mates")
@Tag(name = "Mate", description = "메이트 API")
public class MateController {

  private final MateService mateService;

  @Operation(summary = "메이트 신청자 리스트 조회")
  @GetMapping("/requests/{userId}")
  public ResponseEntity<BaseResponse<List<GetMateRequestResponse>>> getMateRequest(
      @PathVariable Long userId
  ) {
    List<GetMateRequestResponse> result = mateService.getMateRequest(userId);
    return ResponseEntity.ok(BaseResponse.success(result));
  }
}
