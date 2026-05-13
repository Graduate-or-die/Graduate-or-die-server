package server.pome.portfolio.export.latex;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;
import server.pome.portfolio.export.dto.PortfolioExportFormat;
import server.pome.portfolio.export.latex.filedata.LatexDocument;
import server.pome.portfolio.export.latex.filedata.PortfolioLatexItem;
import server.pome.portfolio.export.latex.filedata.PortfolioLatexProfile;
import server.pome.portfolio.export.latex.filedata.PortfolioLatexSection;

@Component
public class PortfolioLatexRenderer {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM");

  // 항목별 렌더링
  public LatexDocument renderType(PortfolioLatexProfile profile, PortfolioLatexSection section) {
    StringBuilder body = new StringBuilder();
    appendSection(body, section, false);
    return document(PortfolioExportFormat.TYPE.fileName(section.type(), "tex"), profile, body.toString());
  }

  // 전체 항목 렌더링
  public LatexDocument renderFull(PortfolioLatexProfile profile, List<PortfolioLatexSection> sections) {
    StringBuilder body = new StringBuilder();
    for (PortfolioLatexSection section : sections) {
      appendSection(body, section, false);
    }
    return document(PortfolioExportFormat.FULL.fileName(null, "tex"), profile, body.toString());
  }

  // 요약 버전 렌더링
  public LatexDocument renderResume(PortfolioLatexProfile profile, List<PortfolioLatexSection> sections) {
    StringBuilder body = new StringBuilder();
    for (PortfolioLatexSection section : sections) {
      appendSection(body, section, true);
    }
    return document(PortfolioExportFormat.RESUME.fileName(null, "tex"), profile, body.toString());
  }

  private LatexDocument document(String fileName, PortfolioLatexProfile profile, String body) {
    return new LatexDocument(fileName, preamble() + header(profile) + body + "\\end{document}\n");
  }

  private String preamble() {
    return """
        \\documentclass[10pt,a4paper]{article}
        \\usepackage[margin=0.65in]{geometry}
        \\usepackage{fontspec}
        \\IfFontExistsTF{Noto Sans CJK KR}{\\setmainfont{Noto Sans CJK KR}}{\\IfFontExistsTF{Malgun Gothic}{\\setmainfont{Malgun Gothic}}{\\IfFontExistsTF{NanumGothic}{\\setmainfont{NanumGothic}}{\\IfFontExistsTF{Noto Sans CJK JP}{\\setmainfont{Noto Sans CJK JP}}{\\IfFontExistsTF{Noto Sans}{\\setmainfont{Noto Sans}}{}}}}}
        \\setlength{\\parindent}{0pt}
        \\setlength{\\parskip}{0pt}
        \\raggedbottom
        \\newcommand{\\psection}[1]{\\vspace{10pt}{\\large\\bfseries #1}\\par\\noindent\\rule{\\linewidth}{0.4pt}\\vspace{5pt}}
        \\pagestyle{empty}
        \\begin{document}
        """;
  }

  private String header(PortfolioLatexProfile profile) {
    String name = firstNonBlank(profile.name(), profile.email(), "Portfolio");
    String meta = joinHeader(profile.email(), profile.job());

    StringBuilder builder = new StringBuilder()
        .append("\\begin{center}\n")
        .append("{\\Huge\\bfseries ").append(escape(name)).append("}\\\\[4pt]\n");

    if (!meta.isBlank()) {
      builder.append("{").append(escape(meta)).append("}\\\\[3pt]\n");
    }

    return builder
        .append("\\end{center}\n")
        .append("\\vspace{-2mm}\n")
        .toString();
  }

  private void appendSection(StringBuilder builder, PortfolioLatexSection section, boolean summaryOnly) {
    if (section == null || !section.hasItems()) {
      return;
    }

    builder.append("\\psection{").append(escape(section.title()).toUpperCase(Locale.ROOT)).append("}\n");
    List<PortfolioLatexItem> items = section.items();
    for (int index = 0; index < items.size(); index++) {
      appendItem(builder, items.get(index), summaryOnly, index < items.size() - 1);
    }
  }

  private void appendItem(
      StringBuilder builder,
      PortfolioLatexItem item,
      boolean summaryOnly,
      boolean addBottomSpacing
  ) {
    boolean hasTitle = item.title() != null && !item.title().isBlank();

    if (hasTitle) {
      builder
          .append("\\textbf{").append(escapeTitle(item.title())).append("}")
          .append(dateRangeText(item))
          .append(summaryOnly ? "\\\\[6pt]\n" : "\\\\[-2pt]\n");
    }

    // resume 요약본은 날짜와 제목만 노출하고, 상세 버전은 subtitle과 details를 bullet로 렌더링함
    if (!summaryOnly && hasBullets(item)) {
      if (hasTitle) {
        builder.append("\\vspace{-5pt}\n");
      }
      builder.append("\\begin{itemize}\n")
          .append("  \\setlength{\\itemsep}{2pt}\n")
          .append("  \\setlength{\\parskip}{0pt}\n")
          .append("  \\setlength{\\parsep}{0pt}\n");
      if (item.subtitle() != null && !item.subtitle().isBlank()) {
        builder.append("  \\item ").append(escapeInline(item.subtitle())).append("\n");
      }
      for (String detail : item.details()) {
        if (detail != null && !detail.isBlank()) {
          builder.append("  \\item ").append(escapeInline(detail)).append("\n");
        }
      }
      builder.append("\\end{itemize}\n");
    }

    if (!summaryOnly && addBottomSpacing) {
      builder.append("\\vspace{3pt}\n");
    }
  }

  private boolean hasBullets(PortfolioLatexItem item) {
    boolean hasSubtitle = item.subtitle() != null && !item.subtitle().isBlank();
    boolean hasDetails = item.details() != null && item.details().stream()
        .anyMatch(detail -> detail != null && !detail.isBlank());
    return hasSubtitle || hasDetails;
  }

  private String dateRange(LocalDate startAt, LocalDate endAt) {
    if (startAt == null && endAt == null) {
      return "";
    }
    if (startAt == null) {
      return formatDate(endAt);
    }
    if (endAt == null) {
      return formatDate(startAt);
    }
    return formatDate(startAt) + " -- " + formatDate(endAt);
  }

  private String formatDate(LocalDate date) {
    return date == null ? "" : DATE_FORMATTER.format(date);
  }

  private String dateRangeText(PortfolioLatexItem item) {
    String dateRange = dateRange(item.startAt(), item.endAt());
    return dateRange.isBlank() ? "" : "\\hfill " + escape(dateRange);
  }

  // 사용자가 입력한 특수문자가 LaTeX 명령으로 해석되지 않도록 치환
  private String escape(String value) {
    if (value == null || value.isBlank()) {
      return "";
    }

    return escapeNormalized(value.strip());
  }

  private String escapeInline(String value) {
    if (value == null || value.isBlank()) {
      return "";
    }

    return escapeNormalized(value.strip()
        .replaceAll("[\\r\\n]+", " ")
        .replaceAll("\\s{2,}", " "));
  }

  private String escapeNormalized(String value) {
    return value
        .replace("\r\n", "\n")
        .replace("\r", "\n")
        .replace("\\", "\\textbackslash{}")
        .replace("&", "\\&")
        .replace("%", "\\%")
        .replace("$", "\\$")
        .replace("#", "\\#")
        .replace("_", "\\_")
        .replace("{", "\\{")
        .replace("}", "\\}")
        .replace("~", "\\textasciitilde{}")
        .replace("^", "\\textasciicircum{}");
  }

  private String escapeTitle(String value) {
    if (value == null || value.isBlank()) {
      return "";
    }

    return value.lines()
        .map(String::strip)
        .filter(line -> !line.isBlank())
        .map(this::escapeNormalized)
        .reduce((left, right) -> left + "}\\\\\n\\textbf{" + right)
        .orElse("");
  }

  private String firstNonBlank(String... values) {
    for (String value : values) {
      if (value != null && !value.isBlank()) {
        return value;
      }
    }
    return "";
  }

  private String joinHeader(String... values) {
    StringBuilder builder = new StringBuilder();
    for (String value : values) {
      if (value == null || value.isBlank()) {
        continue;
      }
      if (!builder.isEmpty()) {
        builder.append(" | ");
      }
      builder.append(value);
    }
    return builder.toString();
  }
}
