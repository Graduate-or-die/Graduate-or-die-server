package server.pome.qualification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import server.pome.global.domain.Qualification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SaveUpdateQualificationResponse {

    @Schema(description = "자격증 ID", example = "1")
    private Long qualificationId;

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

    @Schema(description = "첨부", example = "qulificationimg/url")
    private List<String> qualificationFile = new ArrayList<>();

    public static SaveUpdateQualificationResponse from(Qualification qualification) {
        List<String> files = qualification.getQualificationFile();
        return SaveUpdateQualificationResponse.builder()
                .qualificationId(qualification.getId())
                .qualificationName(qualification.getQualificationName())
                .qualificationOrganization(qualification.getQualificationOrganization())
                .qualificationStartAt(qualification.getQualificationStartAt())
                .qualificationEndAt(qualification.getQualificationEndAt())
                .hasQualificationEndAt(qualification.isHasQualificationEndAt())
                .score(qualification.getScore())
                // 아직은 첨부파일 부분을 구현못하기에 임시적 방어 로직
                .qualificationFile(files == null ? Collections.emptyList() : new ArrayList<>(files))
                .build();
    }
}
