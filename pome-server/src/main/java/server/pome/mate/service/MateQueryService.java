package server.pome.mate.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.etc.repository.EtcRepository;
import server.pome.global.domain.Etc;
import server.pome.global.domain.Portfolio;
import server.pome.global.domain.User;
import server.pome.global.enums.MateRequestStatus;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.mate.repository.MateRepository;
import server.pome.portfolio.dto.response.PreviewResponse;
import server.pome.portfolio.dto.response.VisibilityResponse;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.portfolio.service.PortfolioService;
import server.pome.user.repository.UserRepository;

import java.util.*;
import java.util.stream.LongStream;

import static server.pome.global.exception.BaseResponseStatus.PORTFOLIO_NOT_FOUND;
import static server.pome.global.exception.BaseResponseStatus.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class MateQueryService {

    private final MateRepository mateRepository;
    private final PortfolioService portfolioService;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final EtcRepository etcRepository;

    public static final long START_TYPE = 1L;
    public static final long END_TYPE = 7L;

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

    // 메이트 공개범위 여부 및 미리보기
    public PreviewResponse getVisibilityAndPreview(Long userId, Integer limit,
                                                   List<Long> typeIds) {
        Long mateUserId = getMateUserId(userId);

        Portfolio portfolio = portfolioRepository.findByUser_Id(mateUserId);
        if (portfolio == null) {
            throw new BaseException(PORTFOLIO_NOT_FOUND);
        }

        // 공개범위 리스트
        Map<Long, Boolean> visibilityMap = portfolio.getVisibilityMap();
        List<VisibilityResponse> visibility = new ArrayList<>((int) END_TYPE);
        for (long typeId = START_TYPE; typeId <= END_TYPE; typeId++) {
            boolean isVisible = visibilityMap.getOrDefault(typeId, false);
            visibility.add(new VisibilityResponse(typeId, isVisible));
        }

        List<Long> targets = (typeIds == null || typeIds.isEmpty())
                ? LongStream.rangeClosed(START_TYPE, END_TYPE).boxed().toList()
                : typeIds;

        // 미리보기
        int req = (limit == null ? 3 : limit);
        Map<String, PreviewResponse.PreviewBucket> previews = new LinkedHashMap<>();

        for (Long typeId : targets) {
            int n = capForType(typeId, req);
            if (n == 0) {
                previews.put(typeId.toString(), PreviewResponse.PreviewBucket.empty());
                continue;
            }

            if (typeId == 7L) {
                Etc etc = etcRepository.findByPortfolio(portfolio).orElse(null);

                if (etc == null || etc.getLink() == null || etc.getLink().isEmpty()) {
                    previews.put(typeId.toString(), PreviewResponse.PreviewBucket.empty());
                    continue;
                }

                List<String> links = etc.getLink();

                int toIndex = Math.min(n, links.size());
                List<String> previewLinks = links.subList(0, toIndex);

                List<PreviewResponse.PreviewItem> items = new ArrayList<>(previewLinks.size());

                for (String link : previewLinks) {
                    items.add(PreviewResponse.PreviewItem.builder()
                            .id(etc.getId())
                            .title(link)
                            .awardGrade(null)
                            .build());
                }

                previews.put(typeId.toString(),
                        PreviewResponse.PreviewBucket.builder()
                                .items(items)
                                .build());
                continue;
            }
            // 상위 n개
            List<Object[]> rows = portfolioRepository.findPreviewTopN(portfolio.getId(), typeId, n);

            List<PreviewResponse.PreviewItem> items = new ArrayList<>(rows.size());
            for (Object[] r : rows) {
                items.add(PreviewResponse.PreviewItem.builder()
                        .id((Long) r[0])
                        .title((String) r[1])
                        .awardGrade((String) r[2])
                        .build());
            }

            previews.put(typeId.toString(),
                    PreviewResponse.PreviewBucket.builder()
                            .items(items)
                            .build());
        }

        return PreviewResponse.builder()
                .visibility(visibility)
                .previews(previews)
                .build();

        }
    private int capForType(Long typeId, int requested) {
        return switch (typeId.intValue()) {
            case 1 -> Math.min(1, requested);
            case 2 -> Math.min(2, requested);
            default -> Math.min(3, requested);
        };
    }
}
