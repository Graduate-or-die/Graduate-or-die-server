package server.pome.portfolio.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreviewResponse {

    @Schema(description = "공개여부 리스트")
    private List<VisibilityResponse> visibility;

    @Schema(description = "타입별 상위 3개 미리보기 (key=typeId)")
    private Map<String, PreviewBucket> previews;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PreviewBucket {
        private List<PreviewItem> items;


        public static PreviewBucket empty() {
            return PreviewBucket.builder()
                    .items(List.of())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PreviewItem {
        @Schema(description = "블록 ID") private Long id;
        @Schema(description = "제목") private String title; // 예: 학교이름, 전공, 근무처, 대외활동명 etc,,,
        @Schema(description = "시상등급") private String awardGrade; // 오로지 Awards(typeId=4)의 시상등급만을 위한,,,
    }
}
