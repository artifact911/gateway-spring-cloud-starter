package com.artifact.dto.refresh;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenResponse {

    private String accessToken;
    private String refreshToken;
}
