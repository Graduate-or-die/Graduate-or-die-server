package server.pome.award.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.award.dto.request.SaveAwardRequest;
import server.pome.award.dto.request.UpdateAwardRequest;
import server.pome.award.dto.response.SaveAwardResponse;
import server.pome.award.dto.response.UpdateAwardResponse;
import server.pome.award.repository.AwardRepository;
import server.pome.global.domain.Award;
import server.pome.global.domain.Portfolio;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AwardService {

    private final AwardRepository awardRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    // 수상경력 저장
    public SaveAwardResponse saveAward(Long userId, SaveAwardRequest request) {

        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);

        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        // 일단 임시 요청으로 들어온 URL를 save
        // TODO: S3가 붙으면 request에서의 awardFile이 아닌 S3의 URL로 교체 예정
        Award award = request.toEntity(portfolio);
        awardRepository.save(award);

        return SaveAwardResponse.from(award);
    }

    // 수상경력 수정
    public UpdateAwardResponse updateAward(Long userId, Long awardId, UpdateAwardRequest request) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        Award award = awardRepository.findByIdAndPortfolio_User_Id(awardId, userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.AWARD_NOT_FOUND));

        String awardName = request.getAwardName() != null &&  !request.getAwardName().isEmpty() ? request.getAwardName() : award.getAwardName();
        String awardOrganization = request.getAwardOrganization() != null && !request.getAwardOrganization().isEmpty() ? request.getAwardOrganization() : award.getAwardOrganization();
        LocalDate awardDate = request.getAwardDate() != null ? request.getAwardDate() : award.getAwardDate();
        String awardGrade = request.getAwardGrade() != null && !request.getAwardGrade().isEmpty() ? request.getAwardGrade() : award.getAwardGrade();
        List<String> awardFile = request.getAwardFile() != null && !request.getAwardFile().isEmpty() ? request.getAwardFile() : award.getAwardFile();

        award.updateAward(awardName, awardOrganization, awardDate, awardGrade, awardFile);
        return UpdateAwardResponse.from(award);
    }
}
