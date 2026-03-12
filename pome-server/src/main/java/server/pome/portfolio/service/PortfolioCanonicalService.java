package server.pome.portfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.activity.repository.ActivityRepository;
import server.pome.award.repository.AwardRepository;
import server.pome.education.repository.EducationRepository;
import server.pome.etc.repository.EtcRepository;
import server.pome.experience.repository.ExperienceRepository;
import server.pome.project.repository.ProjectRepository;
import server.pome.qualification.repository.QualificationRepository;

import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class PortfolioCanonicalService {

    private final EducationRepository educationRepository;
    private final ActivityRepository activityRepository;
    private final AwardRepository awardRepository;
    private final QualificationRepository qualificationRepository;
    private final ExperienceRepository experienceRepository;
    private final ProjectRepository projectRepository;

    public String build(Long portfolioId) {

        StringJoiner joiner = new StringJoiner("\n");

        // EDUCATION
        educationRepository.findAllByPortfolio_Id(portfolioId)
                .ifPresent(e -> joiner.add(
                        "EDUCATION: " +
                                safe(e.getSchool()) + " " +
                                safe(e.getMajor()) + " " +
                                safe(e.getDegree())
                ));

        // EXPERIENCE
        experienceRepository.findAllByPortfolio_Id(portfolioId)
                .forEach(e -> joiner.add(
                        "EXPERIENCE: " +
                                safe(e.getWorkplace()) + " " +
                                safe(e.getSpot())
                ));

        // ACTIVITY
        activityRepository.findAllByPortfolio_Id(portfolioId)
                .forEach(a -> joiner.add(
                        "ACTIVITY: " +
                                safe(a.getActivityName()) + " " +
                                safe(a.getActivityRole())
                ));

        // AWARD
        awardRepository.findAllByPortfolio_Id(portfolioId)
                .forEach(a -> joiner.add(
                        "AWARD: " +
                                safe(a.getAwardName()) + " " +
                                safe(a.getAwardGrade())
                ));

        // QUALIFICATION
        qualificationRepository.findAllByPortfolio_Id(portfolioId)
                .forEach(q -> joiner.add(
                        "QUALIFICATION: " +
                                safe(q.getQualificationName())
                ));

        // PROJECT
        projectRepository.findAllByPortfolio_Id(portfolioId)
                .forEach(p -> joiner.add(
                        "PROJECT: " +
                                safe(p.getProjectName()) + " " +
                                safe(p.getProjectRole())
                ));


        return joiner.toString();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
