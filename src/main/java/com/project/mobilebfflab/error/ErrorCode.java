package com.project.mobilebfflab.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    DOWNSTREAM_UNAVAILABLE(
            HttpStatus.SERVICE_UNAVAILABLE,
            "Временная ошибка. Попробуйте позже"
    ),

    REQUEST_TIMEOUT(
            HttpStatus.GATEWAY_TIMEOUT,
            "Превышено время ожидания ответа"
    ),

    INTERNAL_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Внутренняя ошибка сервиса"
    );

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ErrorCode(HttpStatus httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }
}
