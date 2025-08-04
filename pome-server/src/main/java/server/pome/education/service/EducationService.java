package server.pome.education.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.education.dto.response.SaveEducationResponse;
import server.pome.global.domain.Education;
import server.pome.education.repository.EducationRepository;
import server.pome.global.domain.Portfolio;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;

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
                .userId(portfolio.getUser().getId())
                .school(education.getSchool())
                .major(education.getMajor())
                .degree(education.getDegree())
                .build();
    }

    // 학력 삭제
    public void deleteEducation(Long educationId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.EDUCATION_NOT_FOUND));
        educationRepository.delete(education);
    }

}
