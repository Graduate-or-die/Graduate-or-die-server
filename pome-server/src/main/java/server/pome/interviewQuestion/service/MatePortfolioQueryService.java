package server.pome.interviewQuestion.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.global.domain.Portfolio;
import server.pome.global.enums.MateRequestStatus;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.mate.repository.MateRepository;
import server.pome.portfolio.repository.PortfolioRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MatePortfolioQueryService {

    private final MateRepository mateRepository;
    private final PortfolioRepository portfolioRepository;

    public Portfolio getMatePortfolio(Long userId) {

        Long mateUserId = mateRepository
                .findMateIdByUserIdAndStatus(userId, MateRequestStatus.ACCEPTED)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.MATCHING_DISABLED));

        return Optional.ofNullable(portfolioRepository.findByUser_Id(mateUserId))
                .orElseThrow(() -> new BaseException(BaseResponseStatus.MATE_PORTFOLIO_NOT_FOUND));

    }
}
