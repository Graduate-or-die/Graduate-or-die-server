package server.pome.mate.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GetMateRequestResponse {

  @Schema(description = "메이트 신청자 ID", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long mateId;

  @Schema(description = "메이트 신청자 닉네임", example = "구준회", requiredMode = RequiredMode.REQUIRED)
  private String mateNickname;

  public static GetMateRequestResponse from(Long mateId, String mateNickname) {

    return GetMateRequestResponse.builder()
        .mateId(mateId)
        .mateNickname(mateNickname)
        .build();
  }
}
