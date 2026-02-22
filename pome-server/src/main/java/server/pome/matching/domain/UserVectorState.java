package server.pome.matching.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_vector_state")
public class UserVectorState {

  @Id
  @Column(name = "user_id")
  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private EmbeddingStatus embeddingStatus;

  @Column(nullable = false, length = 100)
  private String embeddingVersion;

  @Column(nullable = false)
  private Instant updatedAt;

  @Column(columnDefinition = "TEXT")
  private String lastError;

  public UserVectorState(Long userId, String version) {
    this.userId = userId;
    markPending(version);
  }

  public void markPending(String version) {
    this.embeddingStatus = EmbeddingStatus.PENDING;
    if (version != null) {
      this.embeddingVersion = version;
    }
    this.lastError = null;
    this.updatedAt = Instant.now();
  }

  public void markReady() {
    this.embeddingStatus = EmbeddingStatus.READY;
    this.lastError = null;
    this.updatedAt = Instant.now();
  }

  public void markFailed(String error){
    this.embeddingStatus = EmbeddingStatus.FAILED;
    this.lastError = error == null ? null : error.substring(0, Math.min(2000, error.length()));
    this.updatedAt = Instant.now();
  }
}
