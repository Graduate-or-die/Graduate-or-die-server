package server.pome.user.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT u FROM User u WHERE u.id = :id")
  Optional<User> findUserByIdWithLock(@Param("id") Long id);

  boolean existsByNickName(String nickName);

  User findByNickName(String name);

  // 좋아요 수 증가
  @Modifying
  @Query("UPDATE User u SET u.likeCount = u.likeCount + 1 WHERE u.id = :id")
  int increaseLikeCount(@Param("id") Long id);

  // 좋아요 수 감소
  @Modifying
  @Query("""
      UPDATE User u SET u.likeCount = case 
      WHEN u.likeCount > 0 THEN u.likeCount - 1 ELSE 0 END 
      WHERE u.id = :id""")
  int decreaseLikeCount(@Param("id") Long id);

  @Query("SELECT COALESCE(u.likeCount, 0) FROM User u WHERE u.id = :id")
  int findLikeCountById(@Param("id") Long mateId);

  // 카카오 ID로 사용자 조회
  Optional<User> findByKakaoId(Long kakaoId);

  // 이메일로 사용자 조회
  Optional<User> findByEmail(String email);

  // 이메일 중복 여부 확인 (신규 가입 시 체크용)
  boolean existsByEmail(String email);
}
