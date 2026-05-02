package server.pome.portfolio.export.latex.filedata;

import java.util.List;
import server.pome.global.enums.TypeEnum;
import server.pome.portfolio.export.latex.filedata.PortfolioLatexItem;

public record PortfolioLatexSection(
    TypeEnum type,
    String title,
    List<PortfolioLatexItem> items
) {

  public boolean hasItems() {
    return items != null && !items.isEmpty();
  }
}
