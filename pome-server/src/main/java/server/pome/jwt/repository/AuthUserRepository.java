package server.pome.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.User;
import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<User, Long> {

    // 카카오 ID로 사용자 조회
    Optional<User> findByKakaoId(Long kakaoId);

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 이메일 중복 여부 확인 (신규 가입 시 체크용)
    boolean existsByEmail(String email);
}
