package server.pome.matching.infrastructure.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import server.pome.matching.application.recommend.CandidateFilterPort;
import server.pome.matching.infrastructure.repository.UserMatchRepository;

@Component
@RequiredArgsConstructor
public class CandidateFilterAdapter implements CandidateFilterPort {

  private final NamedParameterJdbcTemplate jdbc;
  private final UserMatchRepository userMatchRepository;

  private static final int CHUNK_SIZE = 900;

  @Override
  public Set<Long> filterEligible(long me, List<Long> candidateIds) {
    if (candidateIds == null || candidateIds.isEmpty()) {
      return Set.of();
    }

    Set<Long> out = new HashSet<>();
    for (int i = 0; i < candidateIds.size(); i += CHUNK_SIZE) {
      int end = Math.min(candidateIds.size(), i + CHUNK_SIZE);
      out.addAll(filterChunk(me, candidateIds.subList(i, end)));
    }
    return out;
  }

  private Set<Long> filterChunk(long me, List<Long> ids) {
    // users 테이블에서 matching=1인 user 제외
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

    List<Long> activeCandidates = jdbc.query(sql, params, (rs, rowNum) -> rs.getLong(1));
    if (activeCandidates.isEmpty()) return Set.of();

    // 매칭 완료된 user 제외
    List<Long> matched = userMatchRepository.findMatchedOpponentIds(me, activeCandidates);
    Set<Long> matchedSet = new HashSet<>(matched);

    Set<Long> eligible = new HashSet<>();
    for (Long id : activeCandidates) {
      if (!matchedSet.contains(id)) eligible.add(id);
    }
    return eligible;
  }
}
