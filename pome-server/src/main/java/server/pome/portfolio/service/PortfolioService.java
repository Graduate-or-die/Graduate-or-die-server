package server.pome.portfolio.service;

import static server.pome.global.exception.BaseResponseStatus.*;
import static server.pome.global.exception.BaseResponseStatus.INVALID_TYPE_ENUM;
import static server.pome.global.exception.BaseResponseStatus.USER_NOT_FOUND;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.util.EnumMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.activity.service.ActivityService;
import server.pome.award.service.AwardService;
import server.pome.etc.repository.EtcRepository;
import server.pome.experience.service.ExperienceService;
import server.pome.global.domain.Etc;
import server.pome.global.domain.Portfolio;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.portfolio.dto.response.GetAllPortfolioResponse;
import server.pome.portfolio.dto.response.GetPortfolioResponse;
import server.pome.portfolio.dto.response.PreviewResponse;
import server.pome.portfolio.dto.response.VisibilityResponse;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.global.enums.TypeEnum;
import server.pome.project.service.ProjectService;
import server.pome.qualification.service.QualificationService;
import server.pome.user.repository.UserRepository;
import java.util.LinkedHashMap;
import java.util.stream.LongStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final EtcRepository etcRepository;
    private final List<PortfolioSectionQueryHandler> handlers;
    private Map<TypeEnum, PortfolioSectionQueryHandler> handlerMap;
    private final ExperienceService experienceService;
    private final ActivityService activityService;
    private final AwardService awardService;
    private final QualificationService qualificationService;
    private final ProjectService projectService;

    private static final long START_TYPE = 1L;
    private static final long END_TYPE = 7L;

    @PostConstruct
    void initHandlerMap() {
        handlerMap = handlers.stream()
            .collect(Collectors.toMap(
                PortfolioSectionQueryHandler::supports,
                Function.identity(),
                (a, b) -> {
                    throw new IllegalStateException(
                        "Duplicate handler for type: " + a.supports()
                            + " (" + a.getClass().getName() + ", " + b.getClass().getName() + ")"
                    );
                },
                () -> new EnumMap<>(TypeEnum.class)
            ));
    }

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
        Portfolio portfolio = getPortfolio(userId);
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
    public PreviewResponse getVisibilityAndPreview(Long userId, Integer limit,
        List<Long> typeIds) {
        Portfolio portfolio = getPortfolio(userId);

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
                    .awardGrade((String) r[2]) // 수상(4)만 값, 나머지 null
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

    // 포트폴리오 항목별 조회
    public Object getPortfolioSection(Long userId, Long typeId) {
        Portfolio portfolio = getPortfolio(userId);
        Long portfolioId = portfolio.getId();

        // 단일 항목 조회
        if (typeId != null) {
            TypeEnum type = TypeEnum.fromId(typeId);
            PortfolioSectionQueryHandler handler = handlerMap.get(type);
            if (handler == null) {
                throw new BaseException(INVALID_TYPE_ENUM);
            }
            Object item = handler.query(portfolioId, userId);
            return new GetPortfolioResponse(type, item);
        }

        // 전체 항목 조회
        Map<TypeEnum, Object> sections = new EnumMap<>(TypeEnum.class);
        for (TypeEnum t : TypeEnum.values()) {
            var handler = handlerMap.get(t);
            if (handler == null) {
                continue;
            }
            sections.put(t, handler.query(portfolioId, userId));
        }

        return new GetAllPortfolioResponse(userId, portfolioId, sections);
    }

    // 포트폴리오 전체 조회
    public GetAllPortfolioResponse getAllPortfolioResponse(Long userId) {
        return (GetAllPortfolioResponse) getPortfolioSection(userId, null);
    }

    public Portfolio getPortfolio(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BaseException(USER_NOT_FOUND);
        }
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (portfolio == null) {
            throw new BaseException(USER_NOT_FOUND);
        }
        return portfolio;
    }

    // 학력은 한 개, 경력은 두 개, 기타는 없음, 그 외는 3개
    private int capForType(Long typeId, int requested) {
        return switch (typeId.intValue()) {
            case 1 -> Math.min(1, requested);
            case 2 -> Math.min(2, requested);
            default -> Math.min(3, requested);
        };
    }

    // 포트폴리오 블록 삭제
    public void deletePortfolioBlock(Long userId, Long typeId, Long blockId) {

        if (typeId == 1L || typeId == 7L) {
            throw new BaseException(PORTFOLIO_BLOCK_DELETE_NOT_ALLOWED);
        }

        switch (TypeEnum.fromId(typeId)) {
            case EXPERIENCES -> experienceService.deleteExperience(blockId, userId);
            case ACTIVITIES -> activityService.delete(blockId, userId);
            case AWARDS -> awardService.deleteAward(blockId, userId);
            case QUALIFICATIONS -> qualificationService.deleteQualification(blockId, userId);
            case PROJECTS -> projectService.deleteProject(blockId, userId);
            default -> throw new BaseException(INVALID_TYPE_ENUM);
        }

    }

    public void deletePortfolioBlocks(Long userId, Long typeId, List<Long> blockIds) {

        if (typeId == 1L || typeId == 7L) {
            throw new BaseException(PORTFOLIO_BLOCK_DELETE_NOT_ALLOWED);
        }

        TypeEnum type = TypeEnum.fromId(typeId);

        for (Long blockId : blockIds) {
            switch (type) {
                case EXPERIENCES -> experienceService.deleteExperience(blockId, userId);
                case ACTIVITIES -> activityService.delete(blockId, userId);
                case AWARDS -> awardService.deleteAward(blockId, userId);
                case QUALIFICATIONS -> qualificationService.deleteQualification(blockId, userId);
                case PROJECTS -> projectService.deleteProject(blockId, userId);
                default -> throw new BaseException(INVALID_TYPE_ENUM);
            }
        }
    }
}
