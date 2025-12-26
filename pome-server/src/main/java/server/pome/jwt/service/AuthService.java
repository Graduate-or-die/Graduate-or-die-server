package server.pome.jwt.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.global.domain.RefreshToken;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.jwt.dto.response.TokenReissueResponse;
import server.pome.jwt.provider.JwtTokenProvider;
import server.pome.user.repository.UserRepository;

import java.time.LocalDateTime;

import static server.pome.global.exception.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public TokenReissueResponse reissue(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BaseException(REQUEST_ERROR);
        }

        // 1) refresh JWT 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BaseException(INVALID_TOKEN);
        }

        // 2) DB에 저장된 refresh 확인
        RefreshToken saved = refreshTokenService.findByTokenOrNull(refreshToken);
        if (saved == null) {
            throw new BaseException(INVALID_TOKEN);
        }

        // 3) 유저 존재 확인
        User user = saved.getUser();

        // 4) 회전: refresh 새로 발급 + DB 갱신
        String newRefresh = jwtTokenProvider.generateRefreshToken(user.getId());
        LocalDateTime newExpireAt = jwtTokenProvider.calcRefreshExpireAt();
        saved.rotate(newRefresh, newExpireAt);

        // 5) access 새로 발급
        String newAccess = jwtTokenProvider.generateAccessToken(user.getId());

        return TokenReissueResponse.builder()
                .accessToken(newAccess)
                .refreshToken(refreshToken)
                .build();
    }

    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        refreshTokenService.deleteByToken(refreshToken);
    }
}
