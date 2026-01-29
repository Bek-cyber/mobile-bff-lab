package com.project.mobilebfflab.error;

public class BffException extends RuntimeException {

    private final ErrorCode errorCode;

    public BffException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
