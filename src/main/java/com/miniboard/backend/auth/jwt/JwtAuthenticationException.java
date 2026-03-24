package com.miniboard.backend.auth.jwt;

import lombok.RequiredArgsConstructor;

public class JwtAuthenticationException extends RuntimeException{

    private final JwtErrorCode errorCode;

    public JwtAuthenticationException(JwtErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public JwtErrorCode getErrorCode() {
        return errorCode;
    }
}
