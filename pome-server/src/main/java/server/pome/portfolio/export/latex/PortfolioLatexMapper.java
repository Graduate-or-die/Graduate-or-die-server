package server.pome.portfolio.export.latex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import server.pome.activity.dto.response.ActivitySectionResponse;
import server.pome.award.dto.response.AwardSectionResponse;
import server.pome.education.dto.response.EducationSectionResponse;
import server.pome.etc.dto.response.EtcSectionResponse;
import server.pome.experience.dto.response.ExperienceSectionResponse;
import server.pome.global.enums.TypeEnum;
import server.pome.portfolio.dto.response.GetAllPortfolioResponse;
import server.pome.portfolio.export.latex.filedata.PortfolioLatexItem;
import server.pome.portfolio.export.latex.filedata.PortfolioLatexSection;
import server.pome.project.dto.response.ProjectSectionResponse;
import server.pome.qualification.dto.response.QualificationSectionResponse;

@Component
public class PortfolioLatexMapper {

  // 포트폴리오 전체 응답을 LaTeX 렌더링용 section 목록으로 변환
  public List<PortfolioLatexSection> toSections(GetAllPortfolioResponse response) {
    Map<TypeEnum, Object> sections = response.sections();
    List<PortfolioLatexSection> result = new ArrayList<>();

    for (TypeEnum type : TypeEnum.values()) {
      result.add(toSection(type, sections.get(type)));
    }

    return result;
  }

  public String label(TypeEnum type) {
    return switch (type) {
      case EDUCATIONS -> "Education";
      case EXPERIENCES -> "Experience";
      case ACTIVITIES -> "Activities";
      case AWARDS -> "Awards";
      case QUALIFICATIONS -> "Qualifications";
      case PROJECTS -> "Projects";
      case ETCS -> "Etc";
    };
  }

  public PortfolioLatexSection toSection(TypeEnum type, Object item) {
    List<PortfolioLatexItem> items = switch (type) {
      case EDUCATIONS -> mapEducations(item);
      case EXPERIENCES -> mapExperiences(item);
      case ACTIVITIES -> mapActivities(item);
      case AWARDS -> mapAwards(item);
      case QUALIFICATIONS -> mapQualifications(item);
      case PROJECTS -> mapProjects(item);
      case ETCS -> mapEtcs(item);
    };

    return new PortfolioLatexSection(type, label(type), items);
  }

  /** 항목별 타이틀 - 세부항목 정의 **/
  private List<PortfolioLatexItem> mapEducations(Object item) {
    if (!(item instanceof EducationSectionResponse response) || response.items() == null) {
      return List.of();
    }

    return response.items().stream()
        .map(education -> new PortfolioLatexItem(
            education.school(),
            joinNonBlank(education.degree(), education.major()),
            null,
            null,
            List.of()
        ))
        .toList();
  }

  private List<PortfolioLatexItem> mapExperiences(Object item) {
    if (!(item instanceof ExperienceSectionResponse response) || response.items() == null) {
      return List.of();
    }

    return response.items().stream()
        .map(experience -> new PortfolioLatexItem(
            experience.workplace(),
            experience.spot(),
            experience.experienceStartAt(),
            experience.experienceEndAt(),
            List.of()
        ))
        .toList();
  }

  private List<PortfolioLatexItem> mapActivities(Object item) {
    if (!(item instanceof ActivitySectionResponse response) || response.items() == null) {
      return List.of();
    }

    return response.items().stream()
        .map(activity -> new PortfolioLatexItem(
            activity.activityName(),
            activity.activityRole(),
            activity.activityStartAt(),
            activity.activityEndAt(),
            nonBlankList(activity.result())
        ))
        .toList();
  }

  private List<PortfolioLatexItem> mapAwards(Object item) {
    if (!(item instanceof AwardSectionResponse response) || response.items() == null) {
      return List.of();
    }

    return response.items().stream()
        .map(award -> new PortfolioLatexItem(
            award.awardName(),
            joinNonBlank(award.awardOrganization(), award.awardGrade()),
            award.awardAt(),
            null,
            List.of()
        ))
        .toList();
  }

  private List<PortfolioLatexItem> mapQualifications(Object item) {
    if (!(item instanceof QualificationSectionResponse response) || response.items() == null) {
      return List.of();
    }

    return response.items().stream()
        .map(qualification -> new PortfolioLatexItem(
            qualification.qualificationName(),
            qualification.qualificationOrganization(),
            qualification.qualificationStartAt(),
            qualification.hasQualificationEndAt() ? qualification.qualificationEndAt() : null,
            List.of()
        ))
        .toList();
  }

  private List<PortfolioLatexItem> mapProjects(Object item) {
    if (!(item instanceof ProjectSectionResponse response) || response.items() == null) {
      return List.of();
    }

    return response.items().stream()
        .map(project -> new PortfolioLatexItem(
            project.projectName(),
            project.projectRole(),
            project.projectStartAt(),
            project.projectEndAt(),
            nonBlankList(project.projectDescription(), project.projectAward())
        ))
        .toList();
  }

  private List<PortfolioLatexItem> mapEtcs(Object item) {
    if (!(item instanceof EtcSectionResponse response) || response.items() == null) {
      return List.of();
    }

    List<PortfolioLatexItem> items = new ArrayList<>();
    for (var etc : response.items()) {
      List<String> links = etc.link() == null ? List.of() : etc.link().stream()
          .filter(link -> link != null && !link.isBlank())
          .toList();

      if (!links.isEmpty()) {
        items.add(new PortfolioLatexItem(
            String.join("\n", links),
            null,
            null,
            null,
            nonBlankList(etc.memo())
        ));
      } else if (etc.memo() != null && !etc.memo().isBlank()) {
        items.add(new PortfolioLatexItem(null, null, null, null, nonBlankList(etc.memo())));
      }
    }

    return items;
  }

  private List<String> nonBlankList(String... values) {
    List<String> result = new ArrayList<>();
    for (String value : values) {
      if (value != null && !value.isBlank()) {
        result.add(value);
      }
    }
    return result;
  }

  private String joinNonBlank(String... values) {
    return String.join(" · ", nonBlankList(values));
  }
}
