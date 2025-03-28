package com.artifact.dto.auth;

import lombok.Data;

@Data
public class AuthorizeRequest {
    private String login;
    private String password;
}
