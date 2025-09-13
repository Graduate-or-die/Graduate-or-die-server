package server.pome.etc.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import server.pome.global.domain.Etc;
import server.pome.global.domain.Portfolio;

@Getter
public class SaveEtcRequest {

    @Schema(description = "신체사항", example = "이상없음")
    private String physicalDetails;

    @Schema(description = "국적", example = "대한민국")
    private String nationality;

    @Schema(description = "링크", example = "https://github.com/")
    private String link;

    @Schema(description = "메모", example = "호주워홀경험있음")
    private String memo;

    public Etc toEntity(Portfolio portfolio) {
        return Etc.builder()
                .portfolio(portfolio)
                .physicalDetails(physicalDetails)
                .nationality(nationality)
                .link(link)
                .memo(memo)
                .build();
    }
}
