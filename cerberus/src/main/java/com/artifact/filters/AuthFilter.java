package com.artifact.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private static final String SECRET_KEY = "art911_my_really_super_secret_key_as_it_possible";
    private static final String SECRET_KEY_BASE_64 = "YXJ0OTExX215X3JlYWxseV9zdXBlcl9zZWNyZXRfa2V5X2FzX2l0X3Bvc3NpYmxl";

    private final SecretKey secretKey;

    public AuthFilter() {
        super(Config.class);
        // Инициализация ключа один раз
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Проверка заголовка Authorization
            if (!request.getHeaders().containsKey("Authorization")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = request
                    .getHeaders()
                    .getFirst("Authorization")
                    .replace("Bearer ", "");

            try {
                Claims claims = parseToken(token);
                Set<String> roles = extractRoles(claims);

                // Проверка доступа
                String path = request.getPath().toString();
                if (!hasAccess(roles, path)) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }

                // Добавление заголовков
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Name", claims.getSubject())
                        .header("X-User-Roles", String.join(",", roles))
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Set<String> extractRoles(Claims claims) {
        List<String> rolesList = claims.get("role", List.class);
        return (rolesList != null) ? new HashSet<>(rolesList) : Collections.emptySet();
    }

    private boolean hasAccess(Set<String> roles, String path) {
        if (path.startsWith("/v1/api/hello") && roles.contains("ADMIN")) {
            return true;
        }
        return false;
    }

    public static class Config {}
}

