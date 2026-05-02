package server.pome.portfolio.export.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.global.domain.User;
import server.pome.global.enums.TypeEnum;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.dto.response.GetAllPortfolioResponse;
import server.pome.portfolio.dto.response.GetPortfolioResponse;
import server.pome.portfolio.export.dto.PortfolioExportFormat;
import server.pome.portfolio.export.latex.filedata.LatexDocument;
import server.pome.portfolio.export.latex.filedata.PdfDocument;
import server.pome.portfolio.export.latex.PortfolioLatexMapper;
import server.pome.portfolio.export.latex.filedata.PortfolioLatexProfile;
import server.pome.portfolio.export.latex.PortfolioLatexRenderer;
import server.pome.portfolio.export.latex.filedata.PortfolioLatexSection;
import server.pome.portfolio.service.PortfolioService;
import server.pome.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioExportService {

  private final PortfolioService portfolioService;
  private final UserRepository userRepository;
  private final PortfolioLatexMapper mapper;
  private final PortfolioLatexRenderer renderer;
  private final LatexCompileService latexCompileService;

  public LatexDocument generateLatex(Long userId, PortfolioExportFormat format, Long typeId) {
    User user = findUser(userId);
    PortfolioLatexProfile profile = toProfile(user);

    return switch (format) {
      case TYPE -> generateTypeLatex(userId, profile, typeId);
      case FULL -> generateFullLatex(userId, profile);
      case RESUME -> generateResumeLatex(userId, profile);
    };
  }

  public PdfDocument generatePdf(Long userId, PortfolioExportFormat format, Long typeId) {
    LatexDocument document = generateLatex(userId, format, typeId);
    return latexCompileService.compile(document);
  }

  // 항목별 LaTeX 코드 생성
  private LatexDocument generateTypeLatex(Long userId, PortfolioLatexProfile profile, Long typeId) {
    TypeEnum.fromId(typeId);
    GetPortfolioResponse response = (GetPortfolioResponse) portfolioService.getPortfolioSection(userId, typeId);
    PortfolioLatexSection section = mapper.toSection(response.type(), response.item());
    return renderer.renderType(profile, section);
  }

  // 전체 항목 LaTeX 코드 생성 (타이틀 + 상세)
  private LatexDocument generateFullLatex(Long userId, PortfolioLatexProfile profile) {
    List<PortfolioLatexSection> sections = findAllSections(userId);
    return renderer.renderFull(profile, sections);
  }

  // 전체 항목 LaTeX 코드 생성 (타이틀)
  private LatexDocument generateResumeLatex(Long userId, PortfolioLatexProfile profile) {
    List<PortfolioLatexSection> sections = findAllSections(userId);
    return renderer.renderResume(profile, sections);
  }

  // 유틸 메서드
  private List<PortfolioLatexSection> findAllSections(Long userId) {
    // 전체 항목의 포트폴리오 조회
    GetAllPortfolioResponse response = portfolioService.getAllPortfolioResponse(userId);
    return mapper.toSections(response);
  }

  private User findUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));
  }

  // 최상단 프로필 구성
  private PortfolioLatexProfile toProfile(User user) {
    return new PortfolioLatexProfile(
        firstNonBlank(user.getUserName(), user.getNickName()),
        user.getEmail(),
        user.getJob(),
        user.getIntroduction()
    );
  }

  private String firstNonBlank(String... values) {
    for (String value : values) {
      if (value != null && !value.isBlank()) {
        return value;
      }
    }
    return "";
  }
}
