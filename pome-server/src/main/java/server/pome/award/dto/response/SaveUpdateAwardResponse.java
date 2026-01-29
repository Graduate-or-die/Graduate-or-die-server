package server.pome.award.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import server.pome.attachment.dto.response.FileResponse;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Award;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
@AllArgsConstructor
public class SaveUpdateAwardResponse {

    @Schema(description = "수상경력 ID", example = "1")
    private Long awardId;

    @Schema(description = "대회명", example = "빨리먹기대회")
    private String awardName;

    @Schema(description = "주최기관", example = "숙명캠퍼스다운사업단")
    private String awardOrganization;

    @Schema(description = "수상일자", example = "2025-09-01")
    private LocalDate awardAt;

    @Schema(description = "시상등급", example = "대상")
    private String awardGrade;

    @Schema(description = "파일")
    private FileResponse file;


    public static SaveUpdateAwardResponse from(Award award, Optional<Attachment> attachment) {

        FileResponse file = attachment
                .map(FileResponse::from)
                .orElse(null);

        return SaveUpdateAwardResponse.builder()
                .awardId(award.getId())
                .awardName(award.getAwardName())
                .awardOrganization(award.getAwardOrganization())
                .awardAt(award.getAwardAt())
                .awardGrade(award.getAwardGrade())
                .file(file)
                .build();
    }
}

