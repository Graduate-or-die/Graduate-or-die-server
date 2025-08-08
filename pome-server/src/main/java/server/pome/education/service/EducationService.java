package server.pome.education.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.education.dto.request.SaveEducationRequest;
import server.pome.education.dto.request.UpdateEducationRequest;
import server.pome.education.dto.response.SaveEducationResponse;
import server.pome.education.dto.response.UpdateEducationResponse;
import server.pome.global.domain.Education;
import server.pome.education.repository.EducationRepository;
import server.pome.global.domain.Portfolio;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.user.repository.UserRepository;
import server.pome.user.service.UserService;

@Service
@RequiredArgsConstructor
@Transactional
public class EducationService {

    private final EducationRepository educationRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    // 학력 저장
    public SaveEducationResponse saveEducation(Long userId, SaveEducationRequest request) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);

        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        if (educationRepository.findByPortfolio(portfolio).isPresent()) {
            throw new BaseException(BaseResponseStatus.DUPLICATE_EDUCATION);
        }

        Education education = new Education(
                null,
                portfolio,
                request.getSchool(),
                request.getMajor(),
                request.getDegree()
        );
        educationRepository.save(education);

        return SaveEducationResponse.builder()
                .educationId(education.getId())
                .school(education.getSchool())
                .major(education.getMajor())
                .degree(education.getDegree())
                .build();
    }

    // 학력 수정
    public UpdateEducationResponse updateEducation(Long userId, UpdateEducationRequest request) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        Education education = educationRepository.findByPortfolio(portfolio)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.EDUCATION_NOT_FOUND));

        String school = request.getSchool() != null && !request.getSchool().isEmpty() ? request.getSchool() : education.getSchool();
        String major = request.getMajor() != null && !request.getMajor().isEmpty() ? request.getMajor() : education.getMajor();
        String degree = request.getDegree() != null && !request.getDegree().isEmpty() ? request.getDegree() : education.getDegree();

        education.updateEducation(school, major, degree);

        return UpdateEducationResponse.from(education);
    }

}
