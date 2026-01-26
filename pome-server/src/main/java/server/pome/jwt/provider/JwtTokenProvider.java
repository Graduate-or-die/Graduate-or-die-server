package server.pome.jwt.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenValidityInSeconds;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long userId) {
        return buildToken(userId, accessTokenValidityInSeconds);
    }

    public String generateRefreshToken(Long userId) {
        return buildToken(userId, refreshTokenValidityInSeconds);
    }

    public long getAccessTokenValidityInSeconds() {
        return accessTokenValidityInSeconds;
    }

    public long getRefreshTokenValidityInSeconds() {
        return refreshTokenValidityInSeconds;
    }

    private String buildToken(Long userId, long validityInSeconds) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInSeconds * 1000);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))  // 토큰 안에 userId 넣기
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); // 여기서 검증됨
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("[JWT] 만료된 토큰");
        } catch (UnsupportedJwtException e) {
            log.warn("[JWT] 지원하지 않는 토큰");
        } catch (MalformedJwtException e) {
            log.warn("[JWT] 형식이 깨진 토큰");
        } catch (SecurityException | IllegalArgumentException e) {
            log.warn("[JWT] 올바르지 않은 토큰");
        }
        return false;
    }

    // 토큰에서 userId 추출
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public LocalDateTime calcRefreshExpireAt() {
        return LocalDateTime.now().plusSeconds(refreshTokenValidityInSeconds);
    }
}
