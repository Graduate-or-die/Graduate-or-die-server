package server.pome.etc.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import server.pome.global.domain.Etc;

public record GetEtcListResponse(
  @Schema(description = "링크", example = "https://github.com/")
  List<String> link,

  @Schema(description = "메모", example = "호주워홀경험있음")
  String memo
) {
  public static GetEtcListResponse from(Etc etc) {
    return new GetEtcListResponse(
        etc.getLink(),
        etc.getMemo()
    );
  }
}
