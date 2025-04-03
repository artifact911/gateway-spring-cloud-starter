package com.artifact.dto.login;

import lombok.Data;

@Data
public class AuthorizeRequest {
    private String login;
    private String password;
}
