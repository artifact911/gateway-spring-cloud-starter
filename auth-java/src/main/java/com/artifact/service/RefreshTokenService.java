package com.artifact.service;

import com.artifact.entity.RefreshToken;
import com.artifact.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.artifact.service.AuthService.REFRESH_TOKEN_EXPIRATION;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(String token, String username) {
        RefreshToken rt = new RefreshToken();
        rt.setToken(token);
        rt.setUsername(username);
        rt.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION));
        refreshTokenRepository.save(rt);
    }

    public boolean validateRefreshToken(String token) {
        return refreshTokenRepository.existsById(token);
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteById(token);
    }
}
