package server.pome.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Experience;
import server.pome.global.domain.Project;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // 사용자(userId)가 소유한 프로젝트(project)만 조회
    Optional<Project> findByIdAndPortfolio_User_Id(Long id, Long userId);
}
