package com.artifact.service;

import com.artifact.dto.login.AuthorizeRequest;
import com.artifact.dto.login.Tokens;
import com.artifact.entity.Role;
import com.artifact.entity.User;
import com.artifact.exception.AuthException;
import com.artifact.exception.UserDBException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    // TODO Убрать хранение ключа тут
    private static final String SECRET_KEY_BASE_64 = "YXJ0OTExX215X3JlYWxseV9zdXBlcl9zZWNyZXRfa2V5X2FzX2l0X3Bvc3NpYmxl";
    private static final long ACCESS_TOKEN_EXPIRATION = 86400000; // 24 часа
    public static final long REFRESH_TOKEN_EXPIRATION = 2592000000L; // 30 дней

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public Tokens authenticate(AuthorizeRequest request) {
        User user = validateCredentials(request);
        Set<String> roles = extractRoles(user);

        String accessToken = generateToken(user.getLogin(), roles, ACCESS_TOKEN_EXPIRATION);
        String refreshToken = generateToken(user.getLogin(), Set.of(), REFRESH_TOKEN_EXPIRATION);

        refreshTokenService.saveRefreshToken(refreshToken, user.getLogin());
        return new Tokens(accessToken, refreshToken);
    }

    public Tokens refresh(String refreshToken) {
        // 1. Валидация refresh токена
        Claims claims = validateRefreshToken(refreshToken);

        // 2. Извлечение данных пользователя
        String login = claims.getSubject();
        User user = getUserIfPresent(login);

        // 3. Инвалидация старого токена
        refreshTokenService.deleteRefreshToken(refreshToken);

        // 4. Генерация новых токенов
        Set<String> roles = extractRoles(user);
        String newAccessToken = generateToken(login, roles, ACCESS_TOKEN_EXPIRATION);
        String newRefreshToken = generateToken(login, Set.of(), REFRESH_TOKEN_EXPIRATION);

        // 5. Сохранение нового refresh токена
        refreshTokenService.saveRefreshToken(newRefreshToken, login);

        return new Tokens(newAccessToken, newRefreshToken);
    }

    private String generateToken(String username, Set<String> roles, long expiration) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY_BASE_64));

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    private Claims validateRefreshToken(String token) {
        // 1. Проверка наличия токена в Redis
        if (!refreshTokenService.validateRefreshToken(token)) {
            throw new AuthException("Invalid refresh token");
        }

        // 2. Валидация подписи и срока действия
        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY_BASE_64)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException("Invalid refresh token");
        }
    }

    private User validateCredentials(AuthorizeRequest request) {
        User user = getUserIfPresent(request.getLogin());
        if (!isValidUser(request, user)) {
            throw new AuthException("Invalid credentials");
        }
        return user;
    }

//    public Tokens authenticate(AuthorizeRequest authorizeRequest) {
//        User user = getUserIfPresent(authorizeRequest);
//        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
//
//        if (isValidUser(authorizeRequest, user)) {
//            return new Tokens(generateToken(user.getLogin(), roles));
//        }
//        throw new AuthException("Invalid credentials");
//    }

    private User getUserIfPresent(String login) {
        List<User> usersByLogin = userService.findByLogin(login);
        if (usersByLogin.size() != 1) {
            throw new UserDBException("Invalid credentials");
        }
        return usersByLogin.getFirst();
    }

    private boolean isValidUser(AuthorizeRequest authorizeRequest, User user) {
        return user.getLogin().equals(authorizeRequest.getLogin()) &&
                user.getPassword().equals(authorizeRequest.getPassword());
    }

    private Set<String> extractRoles(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    // Пример для HMAC-SHA (симметричный ключ)
//    private String generateToken(String username, Set<String> roles) {
//        Claims claims = Jwts.claims()
//                .subject(username)
//                .add("role", roles)
//                .build();
//
//        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY_BASE_64));
//
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION);
//
//        return Jwts.builder()
//                .claims(claims)
//                .issuedAt(now)
//                .expiration(expiryDate)
//                .signWith(key, Jwts.SIG.HS256)
//                .compact();
//    }
}
