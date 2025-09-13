package server.pome.etc.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import server.pome.global.domain.Etc;

@Getter
@Builder
@AllArgsConstructor
public class UpdateEtcResponse {

    @Schema(description = "기타 ID", example = "1")
    private Long etcId;

    @Schema(description = "신체사항", example = "이상없음")
    private String physicalDetails;

    @Schema(description = "국적", example = "대한민국")
    private String nationality;

    @Schema(description = "링크", example = "https://github.com/")
    private String link;

    @Schema(description = "메모", example = "호주워홀경험있음")
    private String memo;

    public static UpdateEtcResponse from(Etc etc) {
        return UpdateEtcResponse.builder()
                .etcId(etc.getId())
                .physicalDetails(etc.getPhysicalDetails())
                .nationality(etc.getNationality())
                .link(etc.getLink())
                .memo(etc.getMemo())
                .build();
    }
}
