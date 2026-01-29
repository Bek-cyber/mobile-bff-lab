package com.project.mobilebfflab.error;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorResponseDto {
    String errorCode;
    String message;
    String traceId;
}
