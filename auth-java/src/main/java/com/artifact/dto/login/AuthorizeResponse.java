package com.artifact.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthorizeResponse {

    private String accessToken;
    private String refreshToken;
}
