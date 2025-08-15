package server.pome.mate.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Mate;
import server.pome.global.domain.User;
import server.pome.global.enums.MateRequestStatus;

@Repository
public interface MateRepository extends JpaRepository<Mate, Long> {

  // targetUser가 받은 메이트 신청 중 특정 status를 가진 Mate 목록 조회
  List<Mate> findByTargetUserAndStatus(User targetUser, MateRequestStatus status);

  // fromUser → targetUser 방향으로 신청한 Mate 요청이 존재하는지 여부
  boolean existsByFromUserAndTargetUser(User fromUser, User targetUser);

  // user가 매칭 완료(ACCEPTED)인 Mate 가지고 있는지 여부
  @Query("""
        SELECT CASE WHEN EXISTS (
          SELECT 1 FROM Mate m
          WHERE m.status = server.pome.global.enums.MateRequestStatus.ACCEPTED
            AND (m.fromUser = :user OR m.targetUser = :user)
        ) THEN true ELSE false END
      """)
  boolean existsAcceptedByUser(@Param("user") User user);

  // fromUser → targetUser 방향의 Mate 요청이 특정 status로 존재하는지 여부
  boolean existsByFromUserAndTargetUserAndStatus(User fromUser, User targetUser,
      MateRequestStatus status);

  // targetUser의 신청자 중 fromUser의 상태를 status로 변경
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
  UPDATE Mate m
  SET m.status = :status
  WHERE m.fromUser = :from AND m.targetUser = :target
""")
  int updateStatus(@Param("from") User fromUser,
      @Param("target") User targetUser,
      @Param("status") MateRequestStatus status);

  // targetUser에게 mate를 신청한 fromUser의 상태(fromStatus)를 특정 상태(toStatus)로 변경
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
        UPDATE Mate m
        SET m.status = :toStatus
        WHERE m.fromUser = :from AND m.targetUser = :target AND m.status = :fromStatus
      """)
  int updateStatusTo(@Param("from") User fromUser,
      @Param("target") User targetUser,
      @Param("fromStatus") MateRequestStatus fromStatus,
      @Param("toStatus") MateRequestStatus toStatus);

  @Modifying
  @Query("""
  update Mate m
     set m.status = :toStatus
   where m.status = :fromStatus
     and (
          (m.fromUser.id = :aId and m.targetUser.id = :bId)
       or (m.fromUser.id = :bId and m.targetUser.id = :aId)
     )
""")
  int updateStatusEitherDirection(@Param("aId") Long aId,
      @Param("bId") Long bId,
      @Param("fromStatus") MateRequestStatus fromStatus,
      @Param("toStatus") MateRequestStatus toStatus);

}
