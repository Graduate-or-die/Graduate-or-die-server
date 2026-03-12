package server.pome.etc.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import server.pome.attachment.dto.response.FileResponse;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Etc;

@Getter
@Builder
@AllArgsConstructor
public class SaveUpdateEtcResponse {

  @Schema(description = "기타 블록 ID", example = "1")
  private Long blockId;

  @Schema(description = "링크", example = "https://github.com/")
  private List<String> link;

  @Schema(description = "메모", example = "호주워홀경험있음")
  private String memo;

  @Schema(description = "파일")
  private FileResponse file;

  public static SaveUpdateEtcResponse from(Etc etc, Optional<Attachment> attachment) {
    FileResponse file = attachment
        .map(FileResponse::from)
        .orElse(null);

    return SaveUpdateEtcResponse.builder()
        .blockId(etc.getId())
        .link(etc.getLink())
        .memo(etc.getMemo())
        .file(file)
        .build();
  }
}
