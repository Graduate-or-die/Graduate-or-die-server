package server.pome.portfolio.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VisibilityResponse {

    @Schema(description = "항목 ID", example = "1")
    private Long typeId;

    @Schema(description = "공개 여부", example = "true")
    private boolean visible;
}
