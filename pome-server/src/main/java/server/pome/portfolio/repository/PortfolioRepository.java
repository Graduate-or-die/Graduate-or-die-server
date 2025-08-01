package server.pome.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Portfolio;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

  Portfolio findByUser_Id(Long userId);
}
