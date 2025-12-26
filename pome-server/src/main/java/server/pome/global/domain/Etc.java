package server.pome.global.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name="etcs")
public class Etc extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @ElementCollection
    @Column(name = "link", nullable = true)
    @Comment("링크")
    private List<String> link = new ArrayList<>();

    @Column(name = "memo", nullable = true)
    @Comment("메모")
    private String memo;

    // 기타 링크 4개 제한
    public void addLink(String link) {
        if (this.link.size() >= 4) {
            throw new BaseException(BaseResponseStatus.ETC_LINK_LIMIT_EXCEEDED);
        }
        this.link.add(link);
    }

    // 기타 정보 업데이트
    public void updateEtc(List<String> link, String memo) {
        this.link.clear();

        if (link != null) {
            if (link.size() > 4) {
                throw new BaseException(BaseResponseStatus.ETC_LINK_LIMIT_EXCEEDED);
            }
            this.link.addAll(link);
        }

        this.memo = memo;
    }
}

