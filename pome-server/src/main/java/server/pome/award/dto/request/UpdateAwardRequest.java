package server.pome.award.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class UpdateAwardRequest {

    @Schema(description = "대회명", example = "빨리먹기대회")
    private String awardName;

    @Schema(description = "주최기관", example = "숙명캠퍼스다운사업단")
    private String awardOrganization;

    @Schema(description = "수상일자", example = "2025-09-01")
    private LocalDate awardDate;

    @Schema(description = "시상등급", example = "대상")
    private String awardGrade;

    @Schema(description = "첨부", example = "awardimg/url")
    private List<String> awardFile = new ArrayList<>();
}
