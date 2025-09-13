package server.pome.etc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Education;
import server.pome.global.domain.Etc;
import server.pome.global.domain.Portfolio;

import java.util.Optional;

@Repository
public interface EtcRepository extends JpaRepository<Etc, Long> {
    Optional<Etc> findByPortfolio(Portfolio portfolio);
}
