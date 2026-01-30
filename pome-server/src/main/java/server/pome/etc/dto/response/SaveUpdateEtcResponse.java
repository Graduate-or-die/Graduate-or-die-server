package server.pome.etc.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import server.pome.global.domain.Etc;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SaveUpdateEtcResponse {

    @Schema(description = "기타 ID", example = "1")
    private Long etcId;

    @Schema(description = "링크", example = "https://github.com/")
    private List<String> link;

    @Schema(description = "메모", example = "호주워홀경험있음")
    private String memo;

    public static SaveUpdateEtcResponse from(Etc etc) {
        return SaveUpdateEtcResponse.builder()
                .etcId(etc.getId())
                .link(etc.getLink())
                .memo(etc.getMemo())
                .build();
    }
}
