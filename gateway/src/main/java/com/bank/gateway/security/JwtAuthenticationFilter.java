package com.bank.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_ROLE_HEADER = "X-User-Role";

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROLE_CLAIM = "role";
    private static final String DEFAULT_ROLE = "ROLE_USER";
    private static final String UNAUTHORIZED_BODY = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\"}";
    private static final String TOKEN_REQUIRED = "JWT token is required";
    private static final String TOKEN_INVALID = "Invalid or expired JWT token";

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/authorization/auth",
            "/api/authorization/users"
    );

    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/actuator",
            "/api/authorization/v3/api-docs",
            "/api/authorization/swagger-ui"
    );

    private final JwtValidator jwtValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        if (isPublic(request)) {
            return chain.filter(exchange);
        }

        final String header = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return unauthorized(exchange, TOKEN_REQUIRED);
        }

        try {
            final Claims claims = jwtValidator.validate(header.substring(BEARER_PREFIX.length()));
            final String role = claims.get(ROLE_CLAIM, String.class);
            final ServerHttpRequest authenticated = request.mutate()
                    .header(USER_ID_HEADER, claims.getSubject())
                    .header(USER_ROLE_HEADER, role == null ? DEFAULT_ROLE : role)
                    .build();
            return chain.filter(exchange.mutate().request(authenticated).build());
        } catch (JwtException | IllegalArgumentException exception) {
            return unauthorized(exchange, TOKEN_INVALID);
        }
    }

    private boolean isPublic(ServerHttpRequest request) {
        if (request.getMethod() == null || "OPTIONS".equals(request.getMethod().name())) {
            return true;
        }
        final String path = request.getURI().getPath();
        if (PUBLIC_PATHS.contains(path)) {
            return "POST".equals(request.getMethod().name());
        }
        return PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        final ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        final byte[] body = String.format(UNAUTHORIZED_BODY, message).getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
