package server.pome.global.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name="etcs")
public class Etc extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(name = "physical_detail", nullable = true)
    @Comment("신체사항")
    private String physicalDetail;

    @Column(name = "nationality", nullable = true)
    @Comment("국적")
    private String nationality;

    @Column(name = "link", nullable = true)
    @Comment("링크")
    private String link;

    @Column(name = "memo", nullable = true)
    @Comment("메모")
    private String memo;

    // 기타 정보 업데이트
    public void updateEtc(String physicalDetail, String nationality, String link, String memo) {
        this.physicalDetail = physicalDetail;
        this.nationality = nationality;
        this.link = link;
        this.memo = memo;
    }
}
