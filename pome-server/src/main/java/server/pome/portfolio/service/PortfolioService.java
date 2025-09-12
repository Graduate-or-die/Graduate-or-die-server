package server.pome.portfolio.service;

import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.chat.repository.ChatFieldRepository;
import server.pome.global.domain.Portfolio;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.dto.response.VisibilityResponse;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.portfolio.type.TypeEnum;
import server.pome.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final ChatFieldRepository chatFieldRepository;


    // 포트폴리오 생성
    public void createInitialPortfolio(User user) {
        Portfolio portfolio = Portfolio.builder()
                .user(user)
                .visibilityMap(TypeEnum.defaultVisibilityMap())
                .build();
        portfolioRepository.save(portfolio);
    }

    // 항목별 공개범위 설정
    public boolean toggleVisible(Long userId, Long typeId) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }
        TypeEnum.fromId(typeId);

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

    // 공개범위 여부 리스트 조회
    public List<VisibilityResponse> getVisibilityList(Long userId) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        Map<Long, Boolean> visibilityMap = portfolio.getVisibilityMap();
        List<VisibilityResponse> responseList = new ArrayList<>();

        for (long typeId = 1L; typeId <= 7L; typeId++) {
            boolean visible = visibilityMap.getOrDefault(typeId, false);
            responseList.add(new VisibilityResponse(typeId, visible));
        }

        return responseList;
    }
}