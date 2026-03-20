package server.pome.like.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

  // 중복 좋아요가 존재하지 않으면 좋아요 저장
  @Modifying
  @Query(value = """
  INSERT IGNORE INTO likes (target_user, from_user, created_at)
  VALUES (:targetId, :fromId, NOW())
  """, nativeQuery = true)
  int saveIfNotExists(@Param("fromId") Long fromId, @Param("targetId") Long targetId);

  // 특정 유저가 대상 유저에게 좋아요를 눌렀는지 여부
  boolean existsByFromUser_IdAndTargetUser_Id(Long fromUserId, Long targetUserId);

  // 특정 유저가 여러 대상 유저 중 좋아요를 누른 유저 ID 목록 조회
  @Query("""
      SELECT l.targetUser.id
      FROM Like l
      WHERE l.fromUser.id = :fromUserId
        AND l.targetUser.id IN :targetUserIds
      """)
  List<Long> findLikedTargetUserIds(@Param("fromUserId") Long fromUserId,
      @Param("targetUserIds") List<Long> targetUserIds);

  // 좋아요 데이터 삭제
  @Modifying
  int deleteByFromUser_IdAndTargetUser_Id(Long userId, Long mateId);
}
