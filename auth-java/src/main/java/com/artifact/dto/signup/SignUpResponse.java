package com.artifact.dto.signup;

import com.artifact.constants.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class SignUpResponse {

    private Status status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    public SignUpResponse(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public SignUpResponse(Status status) {
        this.status = status;
    }
}
