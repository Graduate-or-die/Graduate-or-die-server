package server.pome.user.service;

import static server.pome.global.exception.BaseResponseStatus.*;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import server.pome.attachment.dto.response.UploadedFileInfo;
import server.pome.attachment.repository.AttachmentRepository;
import server.pome.attachment.service.AttachmentService;
import server.pome.attachment.service.AwsS3Service;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.global.domain.Portfolio;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.user.dto.request.UpdateUserRequest;
import server.pome.user.dto.response.*;
import server.pome.user.repository.UserRepository;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final PortfolioRepository portfolioRepository;
  private final AttachmentService attachmentService;
  private final AttachmentRepository attachmentRepository;
  private final AwsS3Service awsS3Service;

  // 회원 정보 조회
  public GetUserResponse getUserInfo(Long userId) {
    User user = findUserById(userId);
    return buildUserResponse(user);
  }

  // 회원 정보 수정
  public UpdateUserResponse updateUserInfo(Long userId, UpdateUserRequest request, List<MultipartFile> files) {
    User user = findUserById(userId);

    // request에 유효한 값이 있는 경우 저장 (없다면 기존 값 사용)
    String name = Optional.ofNullable(request.getUserName()).orElse(user.getUserName());
    String nickname = Optional.ofNullable(request.getNickName()).orElse(user.getNickName());
    boolean matching = Optional.ofNullable(request.getMatching()).orElse(user.getMatching());
    String introduction = Optional.ofNullable(request.getIntroduction()).orElse(user.getIntroduction());
    String job = Optional.ofNullable(request.getJob()).orElse(user.getJob());

    if (Boolean.TRUE.equals(request.getRemoveProfileImage())) {

      if (user.getProfileImage() != null) {
        awsS3Service.deleteFile(user.getProfileImage());
      }

      user.updateProfileImage(null);
    }

    if (Boolean.TRUE.equals(request.getRemoveProfileImage())
            && files != null
            && !files.isEmpty()) {

      throw new BaseException(BaseResponseStatus.INVALID_PROFILE_IMAGE_REQUEST);
    }

    user.updateUserInfo(name, nickname, matching, introduction, job);

    if (files != null && !files.isEmpty()) {

      if (files.size() > 1) {
        throw new BaseException(BaseResponseStatus.FILE_LIMIT_EXCEEDED);
      }
      MultipartFile file = files.get(0);

      awsS3Service.deleteFile(user.getProfileImage());

      UploadedFileInfo uploaded =
              awsS3Service.uploadFile(file, "profile");

      user.updateProfileImage(uploaded.getStoredKey());
    }

    return UpdateUserResponse.from(user);
  }

  // 회원 검색
  public GetUserResponse searchUser (Long userId, String name) {
    User user = findUserById(userId);

    User searchMate = userRepository.findByNickName(name);
    if (searchMate == null) {
      throw new BaseException(USER_NOT_FOUND);
    }

    if (user.getId().equals(searchMate.getId())) {
      throw new BaseException(CANNOT_MATE_SELF_REQUEST);
    }

    return buildUserResponse(searchMate);
  }

  // 공통 응답 생성 메서드
  private GetUserResponse buildUserResponse(User user) {
    Portfolio portfolio = portfolioRepository.findByUser_Id(user.getId());
    //List<String> tags = (portfolio != null) ? portfolio.getTag() : new ArrayList<>();

    return GetUserResponse.from(user);
  }

  // 유저 조회 메서드
  private User findUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
  }
}
