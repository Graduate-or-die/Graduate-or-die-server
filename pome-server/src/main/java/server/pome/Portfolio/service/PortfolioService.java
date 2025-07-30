package server.pome.portfolio.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.global.domain.Portfolio;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.portfolio.type.TypeEnum;
import server.pome.user.repository.UserRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleVisible(Long userId, Long typeId) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (portfolio == null) {
            throw new BaseException(BaseResponseStatus.INVALID_USER);
        }

        Map<Long, Boolean> visibilityMap = portfolio.getVisibilityMap();

        // 1L 2L -> 학력경력
        if (typeId.equals(1L) || typeId.equals(2L)) {
            boolean current = visibilityMap.getOrDefault(1L, false);
            boolean newValue = !current;

            visibilityMap.put(1L, newValue);
            visibilityMap.put(2L, newValue);
            return newValue;
        }

        boolean current = visibilityMap.getOrDefault(typeId, false);
        boolean newValue = !current;
        visibilityMap.put(typeId, newValue);

        return newValue;
    }
}