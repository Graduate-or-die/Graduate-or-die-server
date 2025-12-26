package server.pome.jwt.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.global.domain.RefreshToken;
import server.pome.global.domain.User;
import server.pome.jwt.repository.RefreshTokenRepository;

@RequiredArgsConstructor
@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void save(User user, String refreshToken, LocalDateTime expiresAt) {
        RefreshToken entity = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiresAt(expiresAt)
                .build();
        refreshTokenRepository.save(entity);
    }

    public RefreshToken findByTokenOrNull(String token) {
        return refreshTokenRepository.findByToken(token).orElse(null);
    }

    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    public void deleteAllByUserId(Long userId) {
        refreshTokenRepository.deleteAllByUser_Id(userId);
    }
}
