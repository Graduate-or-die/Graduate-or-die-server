package server.pome.global.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="portfolios")
public class Portfolio extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "portfolio_visibility", joinColumns = @JoinColumn(name = "portfolio_id"))
    @MapKeyColumn(name = "type_id")
    @Column(name = "is_visible")
    @Comment("알람 여부 매핑")
    private Map<Long, Boolean> visibilityMap = new HashMap<>();

    @Version
    @Column(nullable = false)
    @Comment("단조 증가값")
    private Long version;

    @Builder.Default
    @Column(nullable = false)
    private long touch = 0L;

    public void touch() {
        this.touch++;
    }

    public Portfolio(User user, Map<Long, Boolean> visibilityMap) {
        this.user = user;
        this.visibilityMap = visibilityMap;
    }
}
