package server.pome.etc.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.etc.dto.request.SaveEtcRequest;
import server.pome.etc.dto.request.UpdateEtcRequest;
import server.pome.etc.dto.response.SaveUpdateEtcResponse;
import server.pome.etc.repository.EtcRepository;
import server.pome.global.domain.Etc;
import server.pome.global.domain.Portfolio;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EtcService {

    private final EtcRepository etcRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    // 기타 저장
    public SaveUpdateEtcResponse saveEtc(Long userId, SaveEtcRequest request) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);

        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        if (etcRepository.findByPortfolio(portfolio).isPresent()) {
            throw new BaseException(BaseResponseStatus.DUPLICATE_ETC);
        }

        Etc etc = request.toEntity(portfolio);
        etcRepository.save(etc);

        return SaveUpdateEtcResponse.from(etc);
    }

    // 기타 수정
    public SaveUpdateEtcResponse updateEtcResponse(Long userId, UpdateEtcRequest request) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        Etc etc = etcRepository.findByPortfolio(portfolio)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.ETC_NOT_FOUND));

        List<String> link = request.getLink() != null && !request.getLink().isEmpty() ? request.getLink() : etc.getLink();
        String memo = request.getMemo() != null && !request.getMemo().isEmpty() ? request.getMemo() : etc.getMemo();

        etc.updateEtc(link, memo);
        return SaveUpdateEtcResponse.from(etc);
    }
}
