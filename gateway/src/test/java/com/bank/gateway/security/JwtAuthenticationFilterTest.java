package com.bank.gateway.security;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

class JwtAuthenticationFilterTest {

    private static final String SECRET = "bank-ddd-test-jwt-secret-key-32-bytes-minimum";

    private final JwtAuthenticationFilter filter =
            new JwtAuthenticationFilter(new JwtValidator(SECRET));

    private String token(String subject, Duration lifetime) {
        final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        final Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .claim("role", "ROLE_USER")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(lifetime)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Mono<Void> run(MockServerWebExchange exchange, AtomicReference<ServerWebExchange> captured) {
        final GatewayFilterChain chain = current -> {
            captured.set(current);
            return Mono.empty();
        };
        return filter.filter(exchange, chain);
    }

    @Test
    @DisplayName("пропускает публичный POST /auth без токена")
    void shouldAllowPublicAuthPath() {
        final MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/authorization/auth").build());
        final AtomicReference<ServerWebExchange> captured = new AtomicReference<>();

        run(exchange, captured).block();

        assertThat(captured.get()).isNotNull();
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    @Test
    @DisplayName("пропускает регистрацию POST /users без токена")
    void shouldAllowPublicRegistration() {
        final MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/authorization/users").build());
        final AtomicReference<ServerWebExchange> captured = new AtomicReference<>();

        run(exchange, captured).block();

        assertThat(captured.get()).isNotNull();
    }

    @Test
    @DisplayName("возвращает 401 без токена на защищённом пути")
    void shouldRejectMissingToken() {
        final MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/profile/profiles").build());
        final AtomicReference<ServerWebExchange> captured = new AtomicReference<>();

        run(exchange, captured).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(captured.get()).isNull();
        assertThat(exchange.getResponse().getBodyAsString().block()).contains("JWT token is required");
    }

    @Test
    @DisplayName("возвращает 401 для невалидного токена")
    void shouldRejectInvalidToken() {
        final MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/profile/profiles")
                        .header("Authorization", "Bearer broken.token.value")
                        .build());
        final AtomicReference<ServerWebExchange> captured = new AtomicReference<>();

        run(exchange, captured).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(captured.get()).isNull();
    }

    @Test
    @DisplayName("пропускает валидный токен и прокидывает X-User-Id / X-User-Role")
    void shouldPassValidTokenAndAddUserHeaders() {
        final String jwt = token("700", Duration.ofHours(1));
        final MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/profile/profiles")
                        .header("Authorization", "Bearer " + jwt)
                        .build());
        final AtomicReference<ServerWebExchange> captured = new AtomicReference<>();

        run(exchange, captured).block();

        assertThat(captured.get()).isNotNull();
        assertThat(captured.get().getRequest().getHeaders().getFirst("X-User-Id")).isEqualTo("700");
        assertThat(captured.get().getRequest().getHeaders().getFirst("X-User-Role")).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("пропускает CORS preflight OPTIONS")
    void shouldAllowOptionsRequest() {
        final MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.options("/api/profile/profiles").build());
        final AtomicReference<ServerWebExchange> captured = new AtomicReference<>();

        run(exchange, captured).block();

        assertThat(captured.get()).isNotNull();
    }
}
