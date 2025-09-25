package server.pome.portfolio.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.chat.repository.ChatFieldRepository;
import server.pome.global.domain.Portfolio;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.dto.response.PreviewResponse;
import server.pome.portfolio.dto.response.VisibilityResponse;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.portfolio.type.TypeEnum;
import server.pome.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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

    // 공개범위 여부 및 미리보기
    public PreviewResponse getVisibilityAndPreview(Long userId, Integer limit, List<Long> typeIds) {

        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (portfolio == null) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        // 공개범위 리스트
        Map<Long, Boolean> visibilityMap = portfolio.getVisibilityMap();
        List<VisibilityResponse> visibility = new ArrayList<>(7);
        for (long typeId = 1L; typeId <= 7L; typeId++) {
            boolean isVisible = visibilityMap.getOrDefault(typeId, false);
            visibility.add(new VisibilityResponse(typeId, isVisible));
        }

        List<Long> targets = (typeIds == null || typeIds.isEmpty())
                ? java.util.stream.LongStream.rangeClosed(1, 7).boxed().toList()
                : typeIds;

        // 미리보기
        int req = (limit == null ? 3 : limit);
        Map<String, PreviewResponse.PreviewBucket> previews = new java.util.LinkedHashMap<>();

        for (Long typeId : targets) {
            int n = capForType(typeId, req);
            if (n == 0) {
                previews.put(typeId.toString(), PreviewResponse.PreviewBucket.empty());
                continue;
            }

            // 상위 N + total 조회
            List<Object[]> rows = portfolioRepository.findPreviewTopN(portfolio.getId(), typeId, n);
            long total = portfolioRepository.countVisibleByType(portfolio.getId(), typeId);

            java.util.List<PreviewResponse.PreviewItem> items = new java.util.ArrayList<>(rows.size());
            for (Object[] r : rows) {
                items.add(PreviewResponse.PreviewItem.builder()
                        .id((Long) r[0])
                        .title((String) r[1])
                        .awardGrade((String) r[2]) // 수상(4)만 값, 나머지 null
                        .build());
            }

            previews.put(typeId.toString(),
                    PreviewResponse.PreviewBucket.builder()
                            .items(items)
                            .count(items.size())
                            .hasMore(total > items.size())
                            .total(total)
                            .build());
        }

        return PreviewResponse.builder()
                .visibility(visibility)
                .previews(previews)
                .build();
    }

    // 학력은 한 개, 경력은 두 개, 기타는 없음, 그 외는 3개
    private int capForType(Long typeId, int requested) {
        return switch (typeId.intValue()) {
            case 1 -> Math.min(1, requested);
            case 2 -> Math.min(2, requested);
            case 7 -> 0;
            default -> Math.min(3, requested);
        };
    }
}