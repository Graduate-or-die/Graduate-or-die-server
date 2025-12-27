package server.pome.education.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Education;
import server.pome.global.domain.Portfolio;

import java.util.Optional;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    Optional<Education> findByPortfolio(Portfolio portfolio);

    Optional<Education> findAllByPortfolio_Id(Long portfolioId);
}
