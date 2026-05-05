package server.pome.portfolio.export.dto;

import server.pome.global.enums.TypeEnum;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;

public enum PortfolioExportFormat {
  TYPE,     // 항목별 pdf 추출 (ex. export/pdf/type?typeId=6)
  FULL,     // 전체 + 타이틀 + 서브타이틀
  RESUME;   // 전체 + 타이틀만

  public boolean requiresType() {
    return this == TYPE;
  }

  public static PortfolioExportFormat from(String value) {
    if (value == null || value.isBlank()) {
      throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
    }

    try {
      return PortfolioExportFormat.valueOf(value.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
    }
  }

  public String fileName(TypeEnum type, String extension) {
    return switch (this) {
      case TYPE -> "portfolio_" + type.getS3Dir() + "." + extension;
      case FULL -> "portfolio_full." + extension;
      case RESUME -> "resume_summary." + extension;
    };
  }
}
