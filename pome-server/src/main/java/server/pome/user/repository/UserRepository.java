package server.pome.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByNickName(String nickName);
}
