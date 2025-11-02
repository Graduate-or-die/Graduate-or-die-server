package server.pome.education.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.pome.global.domain.Education;
import server.pome.global.domain.Portfolio;

@Getter
@NoArgsConstructor
public class SaveEducationRequest {

    @Schema(description = "학교", example = "숙명여자대학교")
    private String school;

    @Schema(description = "전공", example = "컴퓨터과학전공")
    private String major;

    @Schema(description = "학위", example = "학사")
    private String degree;

    public Education toEntity(Portfolio portfolio) {
        return Education.builder()
                .portfolio(portfolio)
                .school(school)
                .major(major)
                .degree(degree)
                .build();
    }
}
