package com.artifact.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class ErrorBodyResponse {

    private String traceId;
    private String appName;
    private String message;
}
