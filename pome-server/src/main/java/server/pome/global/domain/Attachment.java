package server.pome.global.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "Attachments")
public class Attachment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(nullable = false)
    @Comment("포트폴리오 타입")
    private Long typeId;

    @Column(nullable = false)
    @Comment("타입별 블록 ID")
    private Long blockId;

    @Column(nullable = false)
    @Comment("원본 파일명")
    private String originalFileName;

    @Column(nullable = false)
    @Comment("S3 저장 파일명")
    private String storedFileName;

    @Column(nullable = false)
    @Comment("파일 URI")
    private String fileUrl;

}
