package server.pome.tag.OpenApiTagGeneratorImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import server.pome.tag.OpenApiTagGenerator.PortfolioTagNormalizer;
import server.pome.global.enums.TypeEnum;

import java.util.*;
import server.pome.portfolio.dto.response.GetAllPortfolioResponse;

@Component
public class PortfolioTagNormalizerImpl implements PortfolioTagNormalizer {

  private static final ObjectMapper om = new ObjectMapper();
  private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {};

  @Override
  public String normalize(GetAllPortfolioResponse portfolio) {
    StringBuilder sb = new StringBuilder();
    Map<TypeEnum, Object> sections = portfolio.sections();

    for (TypeEnum type : TypeEnum.values()) {
      Object section = sections.get(type);
      if (section == null) continue;

      sb.append("[").append(type.name()).append("]\n");
      sb.append(convertSectionToText(type, section));
      sb.append("\n\n");
    }
    return sb.toString();
  }

  private String convertSectionToText(TypeEnum type, Object section) {
    return switch (type) {
      case QUALIFICATIONS -> normalizeList(section, this::qualificationLine, "- 없음");
      case EXPERIENCES -> normalizeList(section, this::experienceLine, "- 없음");
      case ACTIVITIES -> normalizeList(section, this::activityLine, "- 없음");
      case AWARDS -> normalizeList(section, this::awardLine, "- 없음");
      case PROJECTS -> normalizeList(section, this::projectLine, "- 없음");
      case ETCS -> normalizeSingleOrList(section, this::etcLine, "- 없음");
      default -> safeToString(section);
    };
  }

  private String normalizeList(Object section,
      LineFormatter formatter,
      String emptyLine) {
    List<Object> list = asList(section);
    if (list.isEmpty()) return emptyLine + "\n";

    StringBuilder sb = new StringBuilder();
    for (Object item : list) {
      String line = formatter.format(item);
      if (line != null && !line.isBlank()) sb.append("- ").append(line).append("\n");
    }
    if (sb.isEmpty()) return emptyLine + "\n";
    return sb.toString();
  }

  private String normalizeSingleOrList(Object section,
      LineFormatter formatter,
      String emptyLine) {
    List<Object> list = asList(section);
    if (!list.isEmpty()) {
      return normalizeList(section, formatter, emptyLine);
    }
    String one = formatter.format(section);
    if (one == null || one.isBlank()) return emptyLine + "\n";
    return "- " + one + "\n";
  }

  private List<Object> asList(Object section) {
    if (section == null) return List.of();
    if (section instanceof List<?> l) return new ArrayList<>(l);
    Map<String, Object> m = toMap(section);
    Object items = m.get("items");
    if (items instanceof List<?> l) return new ArrayList<>(l);
    return List.of();
  }

  private Map<String, Object> toMap(Object obj) {
    if (obj == null) return Map.of();
    if (obj instanceof Map<?, ?> m) {
      Map<String, Object> res = new HashMap<>();
      for (var e : m.entrySet()) res.put(String.valueOf(e.getKey()), e.getValue());
      return res;
    }
    try {
      return om.convertValue(obj, MAP);
    } catch (Exception e) {
      return Map.of();
    }
  }

  private String getStr(Map<String, Object> m, String key) {
    Object v = m.get(key);
    return v == null ? null : String.valueOf(v);
  }

  private String safeToString(Object obj) {
    if (obj == null) return "";
    String s = String.valueOf(obj);
    return s.length() > 1500 ? s.substring(0, 1500) + "..." : s;
  }

  private String joinIfPresent(String... parts) {
    StringBuilder sb = new StringBuilder();
    for (String p : parts) {
      if (p == null || p.isBlank()) continue;
      if (!sb.isEmpty()) sb.append(" | ");
      sb.append(p);
    }
    return sb.toString();
  }

  private String range(String start, String end) {
    if ((start == null || start.isBlank()) && (end == null || end.isBlank())) return null;
    if (end == null || end.isBlank()) return start + " ~";
    if (start == null || start.isBlank()) return "~ " + end;
    return start + " ~ " + end;
  }

  private String qualificationLine(Object item) {
    Map<String, Object> m = toMap(item);
    String name = getStr(m, "qualificationName");
    String org = getStr(m, "qualificationOrganization");
    String start = getStr(m, "qualificationStartAt");
    String end = getStr(m, "qualificationEndAt");
    String score = getStr(m, "score");
    String dates = range(start, end);

    return joinIfPresent(
        joinIfPresent(name, org),
        dates == null ? null : ("기간: " + dates),
        (score == null || "0".equals(score)) ? null : ("등급/점수: " + score)
    );
  }

  private String experienceLine(Object item) {
    Map<String, Object> m = toMap(item);
    String workplace = getStr(m, "workplace");
    String spot = getStr(m, "spot");
    String start = getStr(m, "experienceStartAt");
    String end = getStr(m, "experienceEndAt");
    String dates = range(start, end);

    return joinIfPresent(
        joinIfPresent(workplace, spot),
        dates == null ? null : ("기간: " + dates)
    );
  }

  private String activityLine(Object item) {
    Map<String, Object> m = toMap(item);
    String name = getStr(m, "activityName");
    String role = getStr(m, "activityRole");
    String result = getStr(m, "result");
    String start = getStr(m, "activityStartAt");
    String end = getStr(m, "activityEndAt");
    String dates = range(start, end);

    return joinIfPresent(
        joinIfPresent(name, role),
        dates == null ? null : ("기간: " + dates),
        result == null ? null : ("성과: " + result)
    );
  }

  private String awardLine(Object item) {
    Map<String, Object> m = toMap(item);
    String name = getStr(m, "awardName");
    String org = getStr(m, "awardOrganization");
    String at = getStr(m, "awardAt");
    String grade = getStr(m, "awardGrade");

    return joinIfPresent(
        joinIfPresent(name, org),
        at == null ? null : ("일자: " + at),
        grade == null ? null : ("등급: " + grade)
    );
  }

  private String projectLine(Object item) {
    Map<String, Object> m = toMap(item);
    String name = getStr(m, "projectName");
    String role = getStr(m, "projectRole");
    String desc = getStr(m, "projectDescription");
    String award = getStr(m, "projectAward");
    String start = getStr(m, "projectStartAt");
    String end = getStr(m, "projectEndAt");
    String dates = range(start, end);

    if (desc != null && desc.length() > 120) desc = desc.substring(0, 120) + "...";

    return joinIfPresent(
        joinIfPresent(name, role),
        dates == null ? null : ("기간: " + dates),
        desc == null ? null : ("설명: " + desc),
        award == null ? null : ("성과: " + award)
    );
  }

  private String etcLine(Object item) {
    Map<String, Object> m = toMap(item);
    Object linkObj = m.get("link");
    String memo = getStr(m, "memo");

    String links = null;
    if (linkObj instanceof List<?> l && !l.isEmpty()) {
      List<?> top = l.size() > 4 ? l.subList(0, 4) : l;
      links = "링크: " + top;
    }

    return joinIfPresent(links, memo == null ? null : ("메모: " + memo));
  }

  @FunctionalInterface
  private interface LineFormatter {
    String format(Object item);
  }
}
