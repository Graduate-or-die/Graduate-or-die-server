package server.pome.etc.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import server.pome.attachment.service.AttachmentService;
import server.pome.etc.dto.request.SaveEtcRequest;
import server.pome.etc.dto.request.UpdateEtcRequest;
import server.pome.etc.dto.response.SaveUpdateEtcResponse;
import server.pome.etc.repository.EtcRepository;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Etc;
import server.pome.global.domain.Portfolio;
import server.pome.global.enums.TypeEnum;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.portfolio.service.event.PortfolioUpdateNotifier;
import server.pome.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class EtcService {

  private final EtcRepository etcRepository;
  private final PortfolioRepository portfolioRepository;
  private final UserRepository userRepository;
  private final AttachmentService attachmentService;
  private final PortfolioUpdateNotifier portfolioUpdateNotifier;

  // 기타 저장
  public SaveUpdateEtcResponse saveEtc(
      Long userId,
      SaveEtcRequest request
  ) {
    Portfolio portfolio = portfolioRepository.findByUser_Id(userId);

    if (!userRepository.existsById(userId)) {
      throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
    }

    if (etcRepository.findByPortfolio(portfolio).isPresent()) {
      throw new BaseException(BaseResponseStatus.DUPLICATE_ETC);
    }

    List<String> link = request.getLink();
    if (link != null && link.size() > 4) {
      throw new BaseException(BaseResponseStatus.ETC_LINK_LIMIT_EXCEEDED);
    }

    Etc etc = request.toEntity(portfolio);
    etcRepository.save(etc);

    portfolioUpdateNotifier.notifyUpdated(userId);
    return SaveUpdateEtcResponse.from(etc);
  }

  // 기타 수정
  public SaveUpdateEtcResponse updateEtcResponse(
      Long userId,
      UpdateEtcRequest request
  ) {
    Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
    if (!userRepository.existsById(userId)) {
      throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
    }

    Etc etc = etcRepository.findByPortfolio(portfolio)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.ETC_NOT_FOUND));

    List<String> link = request.getLink() != null && !request.getLink().isEmpty() ? request.getLink() : etc.getLink();
    String memo = request.getMemo() != null && !request.getMemo().isEmpty() ? request.getMemo() : etc.getMemo();

    etc.updateEtc(link, memo);

    portfolioUpdateNotifier.notifyUpdated(userId);
    return SaveUpdateEtcResponse.from(etc);
  }
}
