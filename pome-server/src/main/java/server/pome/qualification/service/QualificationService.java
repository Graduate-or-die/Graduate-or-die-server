package server.pome.qualification.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.global.domain.Portfolio;
import server.pome.global.domain.Qualification;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.qualification.dto.request.SaveQualificationRequest;
import server.pome.qualification.dto.response.SaveQualificationResponse;
import server.pome.qualification.repository.QualificationRepository;
import server.pome.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class QualificationService {

    private final QualificationRepository qulificationRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    // 자격증 저장
    public SaveQualificationResponse saveQualification(Long userId, SaveQualificationRequest request) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);

        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        // 일단 임시 요청으로 들어온 URL를 save
        // TODO: S3가 붙으면 request에서의 qualificationFile이 아닌 S3의 URL로 교체 예정
        Qualification qualification = request.toEntity(portfolio);
        qulificationRepository.save(qualification);

        return SaveQualificationResponse.from(qualification);
    }
}
