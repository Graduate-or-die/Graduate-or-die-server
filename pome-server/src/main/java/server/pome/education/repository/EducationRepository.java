package server.pome.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Education;

import java.util.Optional;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
}
