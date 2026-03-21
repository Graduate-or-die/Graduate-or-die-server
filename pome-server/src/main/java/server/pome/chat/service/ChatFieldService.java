package server.pome.chat.service;

import static server.pome.global.exception.BaseResponseStatus.INVALID_FIELD_KEY;
import static server.pome.global.exception.BaseResponseStatus.INVALID_REQUEST_FORM;
import static server.pome.global.exception.BaseResponseStatus.PORTFOLIO_BLOCK_NOT_FOUND;

import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.activity.repository.ActivityRepository;
import server.pome.award.repository.AwardRepository;
import server.pome.chat.repository.ChatFieldRepository;
import server.pome.education.repository.EducationRepository;
import server.pome.etc.repository.EtcRepository;
import server.pome.experience.repository.ExperienceRepository;
import server.pome.global.domain.ChatField;
import server.pome.global.domain.User;
import server.pome.global.enums.TypeEnum;
import server.pome.global.exception.BaseException;
import server.pome.project.repository.ProjectRepository;
import server.pome.qualification.repository.QualificationRepository;
import server.pome.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChatFieldService {

  private final ChatFieldRepository chatFieldRepository;
  private final UserRepository userRepository;
  private final EducationRepository educationRepository;
  private final ExperienceRepository experienceRepository;
  private final ActivityRepository activityRepository;
  private final AwardRepository awardRepository;
  private final QualificationRepository qualificationRepository;
  private final ProjectRepository projectRepository;
  private final EtcRepository etcRepository;

  // 채팅방 생성 또는 조회
  @Transactional
  public ChatField getOrCreate(Long ownerId, Long typeId, Long blockId, String fieldKey) {
    TypeEnum portfolioType = TypeEnum.fromId(typeId);

    // 유효성 검증
    validateFieldKey(portfolioType, fieldKey);
    validateBlock(ownerId, portfolioType, blockId);

    // 포트폴리오 소유자 ID - 항목 - 블록ID - 필드명으로 field 생성 또는 조회
    return chatFieldRepository.findByOwner_IdAndPortfolioTypeAndBlockIdAndFieldKey(ownerId,
            portfolioType, blockId, fieldKey)
        .orElseGet(() -> {

          // 조회 실패 시 생성
          User ownerRef = userRepository.getReferenceById(ownerId);
          return chatFieldRepository.save(
              new ChatField(ownerRef, portfolioType, blockId, fieldKey));
        });
  }

  // 타입별 허용 필드가 아니면 예외 처리
  private void validateFieldKey(TypeEnum portfolioType, String fieldKey) {
    if (fieldKey == null || fieldKey.isBlank()) {
      throw new BaseException(INVALID_REQUEST_FORM);
    }

    Set<String> allowedFieldKeys = ALLOWED_FIELD_KEYS.get(portfolioType);
    if (allowedFieldKeys == null || !allowedFieldKeys.contains(fieldKey)) {
      throw new BaseException(INVALID_FIELD_KEY);
    }
  }

  // blockId가 실제 포트폴리오 블록을 가리키는지 검증
  private void validateBlock(Long ownerId, TypeEnum portfolioType, Long blockId) {
    if (blockId == null || blockId <= 0L) {
      throw new BaseException(INVALID_REQUEST_FORM);
    }

    boolean exists = switch (portfolioType) {
      case EDUCATIONS -> educationRepository.findByIdAndPortfolio_User_Id(blockId, ownerId).isPresent();
      case EXPERIENCES -> experienceRepository.findByIdAndPortfolio_User_Id(blockId, ownerId).isPresent();
      case ACTIVITIES -> activityRepository.findByIdAndPortfolio_User_Id(blockId, ownerId).isPresent();
      case AWARDS -> awardRepository.findByIdAndPortfolio_User_Id(blockId, ownerId).isPresent();
      case QUALIFICATIONS -> qualificationRepository.findByIdAndPortfolio_User_Id(blockId, ownerId).isPresent();
      case PROJECTS -> projectRepository.findByIdAndPortfolio_User_Id(blockId, ownerId).isPresent();
      case ETCS -> etcRepository.findByIdAndPortfolio_User_Id(blockId, ownerId).isPresent();
    };

    if (!exists) {
      throw new BaseException(PORTFOLIO_BLOCK_NOT_FOUND);
    }
  }

  // 타입별로 채팅이 허용되는 실제 포트폴리오 필드 목록
  private static final Map<TypeEnum, Set<String>> ALLOWED_FIELD_KEYS = Map.of(
      TypeEnum.EDUCATIONS, Set.of("school", "major", "degree"),
      TypeEnum.EXPERIENCES, Set.of("workplace", "spot", "experienceStartAt", "experienceEndAt"),
      TypeEnum.ACTIVITIES,
      Set.of("activityName", "activityRole", "activityStartAt", "activityEndAt", "result"),
      TypeEnum.AWARDS, Set.of("awardName", "awardOrganization", "awardAt", "awardGrade"),
      TypeEnum.QUALIFICATIONS,
      Set.of("qualificationName", "qualificationOrganization", "qualificationStartAt",
          "qualificationEndAt", "hasQualificationEndAt", "score"),
      TypeEnum.PROJECTS,
      Set.of("projectName", "projectStartAt", "projectEndAt", "projectRole",
          "projectDescription", "projectAward"),
      TypeEnum.ETCS, Set.of("link", "memo")
  );
}
