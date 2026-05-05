package server.pome.portfolio.export.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PortfolioExportResponse(
    @Schema(description = "생성된 LaTeX 파일명", example = "resume_summary.tex")
    String fileName,

    @Schema(description = "생성된 LaTeX 원문 코드", example = "\\\\documentclass[10pt,a4paper]{article}\\n...")
    String content
) {}
