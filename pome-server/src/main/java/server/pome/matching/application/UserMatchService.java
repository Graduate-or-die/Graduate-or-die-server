package server.pome.matching.application;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.global.domain.UserMatch;
import server.pome.matching.infrastructure.repository.UserMatchRepository;

@Service
@RequiredArgsConstructor
public class UserMatchService {

  private static final int CHUNK_SIZE = 900;

  private final UserMatchRepository userMatchRepository;
  private final NamedParameterJdbcTemplate jdbc;

  @Transactional
  public void recordMatch(long a, long b) {
    UserMatch pair = UserMatch.of(a, b);
    boolean exists = userMatchRepository.existsByUserLowAndUserHigh(
        pair.getUserLow(),
        pair.getUserHigh()
    );
    if (exists) {
      return;
    }

    // 최종 매칭된 사용자 쌍을 저장
    userMatchRepository.save(pair);
  }

  @Transactional
  public void removeMatch(long a, long b) {
    UserMatch pair = UserMatch.of(a, b);
    // 매칭 해제 시 저장된 사용자 쌍 제거
    userMatchRepository.deleteByUserLowAndUserHigh(
        pair.getUserLow(),
        pair.getUserHigh()
    );
  }

  // 전체 후보 리스트 처리
  @Transactional(readOnly = true)
  public Set<Long> filterEligibleCandidates(long me, List<Long> candidateIds) {
    if (candidateIds == null || candidateIds.isEmpty()) {
      return Set.of();
    }

    Set<Long> out = new HashSet<>();
    // 후보 리스트를 청크 단위로 처리
    for (int i = 0; i < candidateIds.size(); i += CHUNK_SIZE) {
      int end = Math.min(candidateIds.size(), i + CHUNK_SIZE);
      out.addAll(filterEligibleChunk(me, candidateIds.subList(i, end)));
    }
    return out;
  }

  private Set<Long> filterEligibleChunk(long me, List<Long> ids) {
    // 매칭 비활성화 유저와 자기 자신 제외
    String sql = """
        SELECT u.id
        FROM users u
        WHERE u.id IN (:ids)
          AND u.id <> :me
          AND u.matching = 1
        """;

    var params = new MapSqlParameterSource()
        .addValue("ids", ids)
        .addValue("me", me);

    return new HashSet<>(
        jdbc.query(sql, params, (rs, rowNum)
            -> rs.getLong(1))
    );
  }
}
