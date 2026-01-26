package server.pome.tag.OpenApiTagGeneratorImpl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import server.pome.tag.OpenApiTagGenerator.TagPostProcessor;

@Component
public class TagPostProcessorImpl implements TagPostProcessor {

  // #, +, 한글, 영문, 숫자, _, 2~10자 사이
  private static final Pattern VALID = Pattern.compile("^#[\\p{L}0-9_]{2,10}$");

  // (임시 구현) 사용 금지 태그들
  private static final Set<String> BLOCKLIST = Set.of("포트폴리오");
  // 최대 태그 개수
  private static final int MAX_SIZE = 6;
  // # 포함 최대 길이
  private static final int MAX_LEN = 10;

  @Override
  public List<String> process(List<String> raw) {
    if (raw == null || raw.isEmpty()) {
      return List.of();
    }

    // 정규화, 검증, 중복 제거
    LinkedHashSet<String> set = new LinkedHashSet<>();
    for (String r : raw) {
      String tag = normalizeOne(r);
      if (tag == null) {
        continue;
      }
      if (BLOCKLIST.contains(tag)) {
        continue;
      }
      if (!VALID.matcher(tag).matches()) {
        continue;
      }
      set.add(tag);
    }

    List<String> result = new ArrayList<>(set);
    if (result.size() > MAX_SIZE) {
      return result.subList(0, MAX_SIZE);
    }
    return result;
  }

  private String normalizeOne(String r) {
    if (r == null) return null;
    String s = r.trim();

    // #을 앞단에 추가 (백엔드취준생 -> #백엔드취준생)
    if (!s.startsWith("#")) {
      s = "#" + s;
    }

    // 공백 제거
    s = s.replaceAll("\\s+", "");

    // 길이 제한
    if (s.length() > MAX_LEN) {
      return null;
    }
    return s;
  }
}
