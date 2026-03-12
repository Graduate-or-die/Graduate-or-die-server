package server.pome.mate.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.global.domain.Portfolio;
import server.pome.global.domain.User;
import server.pome.global.enums.MateRequestStatus;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.mate.repository.MateRepository;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.portfolio.service.PortfolioService;
import server.pome.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MateQueryService {

    private final MateRepository mateRepository;
    private final PortfolioService portfolioService;
    private final UserRepository userRepository;

    // 메이트 프로필 조회
    public User getMateProfile(Long userId) {

        Long mateUserId = getMateUserId(userId);

        return userRepository.findById(mateUserId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));
    }

    // 메이트 포트폴리오 조회
    public Object getMatePortfolio(Long userId, Long typeId) {

        Long mateUserId = mateRepository
                .findMateIdByUserIdAndStatus(userId, MateRequestStatus.ACCEPTED)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.MATCHING_DISABLED));

        return portfolioService.getPortfolioSection(mateUserId, typeId);
    }

    private Long getMateUserId(Long userId) {
        return mateRepository
                .findMateIdByUserIdAndStatus(userId, MateRequestStatus.ACCEPTED)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.MATCHING_DISABLED));
    }
}
