package server.pome.etc.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Optional;
import server.pome.attachment.dto.response.FileResponse;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Etc;

public record GetEtcListResponse(
  @Schema(description = "기타 블록 ID", example = "1")
  Long blockId,

  @Schema(description = "링크", example = "https://github.com/")
  List<String> link,

  @Schema(description = "메모", example = "입주한 경험이 있음")
  String memo
) {
  public static GetEtcListResponse from(Etc etc) {
    return new GetEtcListResponse(
        etc.getId(),
        etc.getLink(),
        etc.getMemo()
    );
  }
}
