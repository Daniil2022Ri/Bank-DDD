package com.bank.gateway.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtValidatorTest {

    private static final String SECRET = "bank-ddd-test-jwt-secret-key-32-bytes-minimum";

    private final JwtValidator validator = new JwtValidator(SECRET);

    private String token(String subject, Duration lifetime) {
        final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        final Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .claim("role", "ROLE_ADMIN")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(lifetime)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    @DisplayName("validate: возвращает claims валидного токена")
    void shouldReturnClaimsForValidToken() {
        final Claims claims = validator.validate(token("700", Duration.ofHours(1)));

        assertThat(claims.getSubject()).isEqualTo("700");
        assertThat(claims.get("role", String.class)).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("validate: отклоняет подделанный токен")
    void shouldRejectTamperedToken() {
        final String tampered = token("700", Duration.ofHours(1)) + "tampered";

        assertThatThrownBy(() -> validator.validate(tampered)).isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("validate: отклоняет истёкший токен")
    void shouldRejectExpiredToken() {
        final String expired = token("700", Duration.ofSeconds(-1));

        assertThatThrownBy(() -> validator.validate(expired)).isInstanceOf(JwtException.class);
    }
}
