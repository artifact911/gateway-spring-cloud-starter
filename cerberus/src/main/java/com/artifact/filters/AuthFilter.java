package com.artifact.filters;

import com.artifact.configuration.SecurityConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.crypto.SecretKey;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private static final String SECRET_KEY_BASE_64 = "YXJ0OTExX215X3JlYWxseV9zdXBlcl9zZWNyZXRfa2V5X2FzX2l0X3Bvc3NpYmxl";

    private final SecretKey secretKey;
    private final SecurityConfig securityConfig;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public AuthFilter(SecurityConfig securityConfig, KafkaTemplate<String, String> kafkaTemplate) {
        super(Config.class);
        // Инициализация ключа один раз
        this.securityConfig = securityConfig;
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY_BASE_64);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);

        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();

            // Пропускаем публичные пути
            if (isPublicPath(path)) {
                final String publicPathKafkaMsgTemplate = "from: %s to: %s";
                String remoteAddress = getRemoteAddress(exchange.getRequest());
                kafkaTemplate.send("PublicPathTopic",
                        String.format(publicPathKafkaMsgTemplate, remoteAddress, path));

                return chain.filter(exchange);
            }

            // Проверка токена для остальных путей
            if (!exchange.getRequest().getHeaders().containsKey("Authorization")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

                final String noTokenPrivatePathKafkaMsgTemplate = "no token request from: %s to: %s";
                String remoteAddress = getRemoteAddress(exchange.getRequest());
                kafkaTemplate.send("PrivatePathTopic",
                        String.format(noTokenPrivatePathKafkaMsgTemplate, remoteAddress, path));

                return exchange.getResponse().setComplete();
            }

            String token = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization")
                    .replace("Bearer ", "");

            Claims claims = parseToken(token);
            Set<String> userRoles = extractRoles(claims);
            String subject = claims.getSubject();

            // Проверка ролей
            if (!hasAccess(path, userRoles)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);

                final String forbiddenPrivatePathKafkaMsgTemplate = "forbidden for %s request from: %s to: %s";
                String remoteAddress = getRemoteAddress(exchange.getRequest());
                kafkaTemplate.send("PrivatePathTopic",
                        String.format(forbiddenPrivatePathKafkaMsgTemplate, subject, remoteAddress, path));

                return exchange.getResponse().setComplete();
            }

            final String successPrivatePathKafkaMsgTemplate = "success for %s request from: %s to: %s";
            String remoteAddress = getRemoteAddress(exchange.getRequest());
            kafkaTemplate.send("PrivatePathTopic",
                    String.format(successPrivatePathKafkaMsgTemplate, subject, remoteAddress, path));

            // Продолжаем цепочку фильтров
            return chain.filter(exchange.mutate()
                    .request(addHeaders(exchange.getRequest(), claims))
                    .build());
        };
    }

    private boolean isPublicPath(String path) {
        return securityConfig.getPublicPaths().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private boolean hasAccess(String path, Set<String> userRoles) {
        return securityConfig.getRolePaths().stream()
                .filter(rolePath -> pathMatcher.match(rolePath.getPath(), path))
                .allMatch(rolePath -> userRoles.stream()
                        .anyMatch(rolePath.getRoles()::contains));
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Set<String> extractRoles(Claims claims) {
        List<String> rolesList = claims.get("roles", List.class);
        return (rolesList != null) ? new HashSet<>(rolesList) : Collections.emptySet();
    }

    private ServerHttpRequest addHeaders(ServerHttpRequest request, Claims claims) {
        String username = claims.getSubject();
        Set<String> roles = extractRoles(claims);

        return request.mutate()
                .header("X-User-Name", username)                    // Логин пользователя
                .header("X-User-Roles", String.join(",", roles))    // Роли через запятую
                .header("X-User-Id", claims.getOrDefault("userId", "").toString())  // Доп. параметры
                .build();
    }

    private String getRemoteAddress(ServerHttpRequest request) {
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        return remoteAddress != null ? remoteAddress.getHostString() : "unknown";
    }

    /**
     * Архитектура Spring Cloud Gateway
     * Классы фильтров, наследующие AbstractGatewayFilterFactory, обязаны иметь:
     * Вложенный класс конфигурации (в вашем случае Config)
     * Указание этого класса в generic-типе: AbstractGatewayFilterFactory<AuthFilter.Config>
     * <p>
     * Для чего используется Config
     * Класс предназначен для кастомизации фильтра через YAML. Даже если сейчас он пустой, его наличие обязательно.
     * <p>
     * Пример использования конфигурации:
     * <p>
     * public static class Config {
     * private boolean logHeaders;
     * private String customParam;
     * // Геттеры и сеттеры
     * }
     * <p>
     * Тогда в application.yml можно будет настроить:
     * <p>
     * spring:
     * cloud:
     * gateway:
     * default-filters:
     * - name: AuthFilter
     * args:
     * logHeaders: true
     * customParam: "value"
     */
    public static class Config {
    }
}