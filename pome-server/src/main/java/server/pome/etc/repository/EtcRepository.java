package server.pome.etc.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Etc;
import server.pome.global.domain.Portfolio;

import java.util.Optional;

@Repository
public interface EtcRepository extends JpaRepository<Etc, Long> {

    Optional<Etc> findByPortfolio(Portfolio portfolio);

    @EntityGraph(attributePaths = {"link"})
    Optional<Etc> findAllByPortfolio_Id(Long portfolioId);
}
