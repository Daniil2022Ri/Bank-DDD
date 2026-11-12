package com.bank.authorization.security;

import com.bank.authorization.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtils {

    private static final String ROLE_CLAIM = "role";

    private final SecretKey secretKey;
    private final Duration lifetime;

    public JwtTokenUtils(@Value("${jwt.secret}") String secret, @Value("${jwt.lifetime}") Duration lifetime) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.lifetime = lifetime;
    }

    public String generateToken(User user) {
        final Instant issuedAt = Instant.now();
        return Jwts.builder()
                .setSubject(String.valueOf(user.getProfileId()))
                .claim(ROLE_CLAIM, user.getRole().name())
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(issuedAt.plus(lifetime)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getRole(Claims claims) {
        return claims.get(ROLE_CLAIM, String.class);
    }

    public long getLifetimeSeconds() {
        return lifetime.toSeconds();
    }
}
