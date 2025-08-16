package server.pome.activity.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import server.pome.global.domain.Activity;
import server.pome.global.domain.Portfolio;

import java.time.LocalDate;

@Getter
public class SaveActivityRequest {

    @Schema(description = "활동명", example = "2025AI해커톤")
    private String activityName;

    @Schema(description = "역할", example = "PM")
    private String activityRole;

    @Schema(description = "활동 시작일", example = "2025-09-01")
    private LocalDate activityStartAt;

    @Schema(description = "활동 마감일", example = "2025-09-02")
    private LocalDate activityEndAt;

    @Schema(description = "성과", example = "우수상")
    private String result;

    public Activity toEntity(Portfolio portfolio) {
        return Activity.builder()
                .portfolio(portfolio)
                .activityName(activityName)
                .activityRole(activityRole)
                .activityStartAt(activityStartAt)
                .activityEndAt(activityEndAt)
                .result(result)
                .build();
    }
}
