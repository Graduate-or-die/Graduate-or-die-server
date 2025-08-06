package server.pome.experience.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Experience;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {
}
