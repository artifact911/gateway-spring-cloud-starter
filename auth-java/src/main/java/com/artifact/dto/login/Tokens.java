package com.artifact.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Tokens {
    private String accessToken;
    private String refreshToken;
}
