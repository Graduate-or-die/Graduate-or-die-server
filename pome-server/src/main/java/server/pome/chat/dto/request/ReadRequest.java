package server.pome.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.pome.portfolio.type.TypeEnum;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadRequest {

  @Schema(description = "항목명", example = "ACTIVITY")
  private TypeEnum portfolioType;

  @Schema(description = "블록 ID", example = "1")
  private Long blockId;

  @Schema(description = "필드명", example = "역할")
  private String fieldKey;

}
