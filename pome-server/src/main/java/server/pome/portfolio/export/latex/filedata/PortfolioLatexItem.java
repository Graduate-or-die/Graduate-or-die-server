package server.pome.portfolio.export.latex.filedata;

import java.time.LocalDate;
import java.util.List;

public record PortfolioLatexItem(
    String title,
    String subtitle,
    LocalDate startAt,
    LocalDate endAt,
    List<String> details
) {}
