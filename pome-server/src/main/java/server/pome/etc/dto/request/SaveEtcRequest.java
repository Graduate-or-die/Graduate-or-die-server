package server.pome.etc.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import server.pome.global.domain.Etc;
import server.pome.global.domain.Portfolio;
import java.util.List;

@Getter
public class SaveEtcRequest {

    @Schema(description = "링크", example = "[\"https://github.com/\"]")
    private List<String> link;

    @Schema(description = "메모", example = "호주워홀경험있음")
    private String memo;

    public Etc toEntity(Portfolio portfolio) {
        return Etc.builder()
                .portfolio(portfolio)
                .link(link)
                .memo(memo)
                .build();
    }
}
