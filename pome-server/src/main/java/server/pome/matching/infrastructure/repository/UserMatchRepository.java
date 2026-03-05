package server.pome.matching.infrastructure.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import server.pome.global.domain.UserMatch;

public interface UserMatchRepository extends JpaRepository<UserMatch, Long> {

  boolean existsByUserLowAndUserHigh(Long userLow, Long userHigh);

  @Query("""
      select case
        when m.userLow = :me then m.userHigh
        else m.userLow
      end
      from UserMatch m
      where (m.userLow = :me and m.userHigh in :candidateIds)
         or (m.userHigh = :me and m.userLow in :candidateIds)
      """)
  List<Long> findMatchedOpponentIds(
      @Param("me") Long me,
      @Param("candidateIds") List<Long> candidateIds
  );


  void deleteByUserLowAndUserHigh(Long userLow, Long userHigh);
}

