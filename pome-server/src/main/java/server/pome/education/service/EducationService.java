package server.pome.education.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.education.dto.request.UpdateEducationRequest;
import server.pome.education.dto.response.SaveEducationResponse;
import server.pome.education.dto.response.UpdateEducationResponse;
import server.pome.global.domain.Education;
import server.pome.education.repository.EducationRepository;
import server.pome.global.domain.Portfolio;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.user.dto.request.UpdateUserRequest;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EducationService {

    private final EducationRepository educationRepository;
    private final PortfolioRepository portfolioRepository;

    // 학력 저장
    public SaveEducationResponse saveEducation(Long userId) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        Education education = new Education(
                null,
                portfolio,
                null,
                null,
                null
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
    public UpdateEducationResponse updateEducation(Long educationId, UpdateEducationRequest request) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.EDUCATION_NOT_FOUND));

        String school = Optional.ofNullable(education.getSchool()).orElse(request.getSchool());
        String major = Optional.ofNullable(education.getMajor()).orElse(request.getMajor());
        String degree = Optional.ofNullable(education.getDegree()).orElse(request.getDegree());

        education.updateEducation(school, major, degree);

        return UpdateEducationResponse.from(education);
    }

}
