package server.pome.portfolio.export;

import io.swagger.v3.oas.annotations.Operation;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;
import server.pome.portfolio.export.dto.PortfolioExportFormat;
import server.pome.portfolio.export.dto.PortfolioExportResponse;
import server.pome.portfolio.export.latex.filedata.LatexDocument;
import server.pome.portfolio.export.latex.filedata.PdfDocument;
import server.pome.portfolio.export.service.PortfolioExportService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/portfolios/export")
public class PortfolioExportController {

  private final PortfolioExportService portfolioExportService;

  @Operation(summary = "포트폴리오 LaTeX 코드 생성")
  @GetMapping("/latex/{format}")
  public ResponseEntity<BaseResponse<PortfolioExportResponse>> generateLatex(
      Authentication authentication,
      @PathVariable String format,
      @RequestParam(required = false) Long typeId
  ) {
    User user = (User) authentication.getPrincipal();
    PortfolioExportFormat exportFormat = PortfolioExportFormat.from(format);
    LatexDocument document = portfolioExportService.generateLatex(user.getId(), exportFormat, typeId);

    return ResponseEntity.ok(BaseResponse.success(
        new PortfolioExportResponse(document.fileName(), document.source())
    ));
  }

  @Operation(summary = "포트폴리오 PDF 생성")
  @GetMapping("/pdf/{format}")
  public ResponseEntity<InputStreamResource> generatePdf(
      Authentication authentication,
      @PathVariable String format,
      @RequestParam(required = false) Long typeId
  ) {
    User user = (User) authentication.getPrincipal();
    PortfolioExportFormat exportFormat = PortfolioExportFormat.from(format);
    PdfDocument document = portfolioExportService.generatePdf(user.getId(), exportFormat, typeId);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentLength(document.content().length);
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDisposition(ContentDisposition.attachment()
        .filename(document.fileName(), StandardCharsets.UTF_8)
        .build());

    return ResponseEntity.ok()
        .headers(headers)
        .body(new InputStreamResource(new ByteArrayInputStream(document.content())));
  }
}
