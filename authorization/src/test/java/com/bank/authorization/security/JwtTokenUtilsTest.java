package com.bank.authorization.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bank.authorization.entity.Role;
import com.bank.authorization.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenUtilsTest {

    private static final String SECRET = "bank-ddd-test-jwt-secret-key-32-bytes-minimum";
    private static final Long PROFILE_ID = 100L;

    private JwtTokenUtils tokenUtils(Duration lifetime) {
        return new JwtTokenUtils(SECRET, lifetime);
    }

    private User user() {
        return User.builder()
                .id(1L)
                .profileId(PROFILE_ID)
                .role(Role.ROLE_ADMIN)
                .build();
    }

    @Test
    @DisplayName("generateToken: токен содержит профиль и роль")
    void shouldGenerateValidToken() {
        final JwtTokenUtils tokenUtils = tokenUtils(Duration.ofHours(1));

        final String token = tokenUtils.generateToken(user());
        final Claims claims = tokenUtils.parseClaims(token);

        assertThat(claims.getSubject()).isEqualTo(String.valueOf(PROFILE_ID));
        assertThat(tokenUtils.getRole(claims)).isEqualTo(Role.ROLE_ADMIN.name());
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    @DisplayName("parseClaims: отклоняет подделанный токен")
    void shouldRejectTamperedToken() {
        final JwtTokenUtils tokenUtils = tokenUtils(Duration.ofHours(1));
        final String token = tokenUtils.generateToken(user());

        assertThatThrownBy(() -> tokenUtils.parseClaims(token + "tampered"))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("parseClaims: отклоняет истёкший токен")
    void shouldRejectExpiredToken() {
        final JwtTokenUtils tokenUtils = tokenUtils(Duration.ofSeconds(-1));
        final String token = tokenUtils.generateToken(user());

        assertThatThrownBy(() -> tokenUtils.parseClaims(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("getLifetimeSeconds: возвращает время жизни токена")
    void shouldReturnLifetimeSeconds() {
        assertThat(tokenUtils(Duration.ofHours(1)).getLifetimeSeconds()).isEqualTo(3600L);
    }
}
