package server.pome.etc.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.util.List;

@Getter
public class UpdateEtcRequest {

    @Schema(description = "링크", example = "[\"https://github.com/\"]")
    private List<String> link;

    @Schema(description = "메모", example = "호주워홀경험있음")
    private String memo;
}
