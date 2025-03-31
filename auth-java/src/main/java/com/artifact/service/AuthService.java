package com.artifact.service;

import com.artifact.dto.auth.AuthorizeRequest;
import com.artifact.dto.auth.Tokens;
import com.artifact.entity.Role;
import com.artifact.entity.User;
import com.artifact.exception.AuthException;
import com.artifact.exception.UserDBException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    // TODO Убрать хранение ключа тут
    private static final String SECRET_KEY = "art911_my_really_super_secret_key_as_it_possible";
    private static final String SECRET_KEY_BASE_64 = "YXJ0OTExX215X3JlYWxseV9zdXBlcl9zZWNyZXRfa2V5X2FzX2l0X3Bvc3NpYmxl";
//    private static final long FIVE_MINUTES_EXPIRATION_TIME = 300000; // 5 мин
    private static final long FIVE_MINUTES_EXPIRATION_TIME = 86400000; // 24 часа

    private final UserService userService;

    public Tokens authenticate(AuthorizeRequest authorizeRequest) {
        User user = getUserIfPresent(authorizeRequest);
        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        if (isValidUser(authorizeRequest, user)) {
            return new Tokens(generateToken(user.getLogin(), roles));
        }
        throw new AuthException("Invalid credentials");
    }

    private boolean isValidUser(AuthorizeRequest authorizeRequest, User user) {
        return user.getLogin().equals(authorizeRequest.getLogin()) &&
                user.getPassword().equals(authorizeRequest.getPassword());
    }

    private User getUserIfPresent(AuthorizeRequest authorizeRequest) {
        List<User> usersByLogin = userService.findByLogin(authorizeRequest.getLogin());
        if (usersByLogin.size() != 1) {
            throw new UserDBException("Invalid credentials");
        }
        return usersByLogin.getFirst();
    }

    // Пример для HMAC-SHA (симметричный ключ)
    private String generateToken(String username, Set<String> roles) {
        Claims claims = Jwts.claims()
                .subject(username)
                .add("role", roles)
                .build();

//        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY_BASE_64));
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + FIVE_MINUTES_EXPIRATION_TIME);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    // Для RS256
//    private String generateToken(String username, String role) {
//        Claims claims = Jwts.claims().subject(username).build();
//        PrivateKey privateKey = getPrivateKey();
//
//        claims.put("role", role);
//
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
//
//        return Jwts.builder()
//                .claims(claims)
//                .issuedAt(now)
//                .expiration(expiryDate)
//                .signWith(privateKey, Jwts.SIG.RS256)
//                .compact();
//    }

//    @SneakyThrows
//    private PrivateKey getPrivateKey() {
//        // 1. Создаем генератор ключей для RSA
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//        keyPairGenerator.initialize(2048);
//
//        // 2. Генерируем пару ключей
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//
//        // 3. Получаем приватный ключ
//        PrivateKey privateKey = keyPair.getPrivate();
//
//        return privateKey;
//    }
}
