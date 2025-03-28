package com.artifact.controller;

import com.artifact.dto.auth.AuthorizeRequest;
import com.artifact.dto.auth.AuthorizeResponse;
import com.artifact.dto.auth.Tokens;
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
    public ResponseEntity<?> signUp(@RequestBody AuthorizeRequest authorizeRequest) {
        try {
            Tokens tokens = authService.authenticate(authorizeRequest);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(AuthorizeResponse.builder().accessToken(tokens.getAccessToken()).build());
        } catch (AuthApiJavaException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(AuthorizeResponse.builder().message(e.getMessage()).build());
        }
    }
}
