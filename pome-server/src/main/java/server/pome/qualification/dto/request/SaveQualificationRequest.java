package server.pome.qualification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import server.pome.global.domain.Portfolio;
import server.pome.global.domain.Qualification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class SaveQualificationRequest {

    @Schema(description = "자격증명", example = "정보처리기사")
    private String qualificationName;

    @Schema(description = "발급기관", example = "한국산업인력공단")
    private String qualificationOrganization;

    @Schema(description = "취득일자", example = "2024-01-01")
    private LocalDate qualificationStartAt;

    @Schema(description = "만료일자", example = "2027-01-01")
    private LocalDate qualificationEndAt;

    @Schema(description = "만료일 여부", example = "true")
    private boolean hasQualificationEndAt;

    @Schema(description = "등급/점수", example = "1")
    private int score;

    public Qualification toEntity(Portfolio portfolio) {
        return Qualification.builder()
                .portfolio(portfolio)
                .qualificationName(qualificationName)
                .qualificationOrganization(qualificationOrganization)
                .qualificationStartAt(qualificationStartAt)
                .qualificationEndAt(qualificationEndAt)
                .hasQualificationEndAt(hasQualificationEndAt)
                .score(score)
                .build();
    }

}
