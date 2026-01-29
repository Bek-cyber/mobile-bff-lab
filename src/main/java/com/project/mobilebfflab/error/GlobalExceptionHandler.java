package com.project.mobilebfflab.error;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BffException.class)
    public ResponseEntity<ErrorResponseDto> handleBffException(BffException ex) {
        ErrorCode code = ex.getErrorCode();

        log.warn("BFF error: {}", code.name(), ex);

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(buildResponse(code));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception ex) {
        log.error("Unexpected error in BFF", ex);

        return ResponseEntity
                .status(ErrorCode.INTERNAL_ERROR.getHttpStatus())
                .body(buildResponse(ErrorCode.INTERNAL_ERROR));
    }

    private ErrorResponseDto buildResponse(ErrorCode code) {
        return ErrorResponseDto.builder()
                .errorCode(code.name())
                .message(code.getDefaultMessage())
                .traceId(MDC.get("traceId"))
                .build();
    }
}
