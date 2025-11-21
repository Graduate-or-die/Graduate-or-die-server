package server.pome.global.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret = "5c1ecc65746183f206b63c0b22971c924b9cc7019f1f370040ebb3f5d2c4a96020c70f8faac37c77bd007b327b5386c6b630a0ebae207d88d730343e0f71d098";

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
}
