package server.pome.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import server.pome.portfolio.type.TypeEnum;

@Getter
public class UnreadRequest {

  @Schema(description = "항목명", example = "ACTIVITY")
  private TypeEnum portfolioType;

}
