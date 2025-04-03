package com.artifact.controller;

import com.artifact.dto.error.ErrorBodyResponse;
import com.artifact.dto.login.AuthorizeRequest;
import com.artifact.dto.login.AuthorizeResponse;
import com.artifact.dto.login.Tokens;
import com.artifact.dto.refresh.RefreshTokenRequest;
import com.artifact.dto.refresh.RefreshTokenResponse;
import com.artifact.exception.AuthApiJavaException;
import com.artifact.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthorizeRequest authorizeRequest) {
        try {
            Tokens tokens = authService.authenticate(authorizeRequest);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new AuthorizeResponse(tokens.getAccessToken(), tokens.getRefreshToken()));
        } catch (AuthApiJavaException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ErrorBodyResponse.builder().message(e.getMessage()).build());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        try {
            Tokens refreshed = authService.refresh(request.getRefreshToken());

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new RefreshTokenResponse(refreshed.getAccessToken(), refreshed.getRefreshToken()));
        } catch (AuthApiJavaException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ErrorBodyResponse.builder().message(e.getMessage()).build());
        }
    }
}
