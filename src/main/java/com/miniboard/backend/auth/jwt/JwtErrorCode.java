package com.miniboard.backend.auth.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum JwtErrorCode {

    INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN("EXPIRED_TOKEN", "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN("UNSUPPORTED_TOKEN", "지원하지 않는 토큰입니다."),
    EMPTY_TOKEN("EMPTY_TOKEN", "토큰이 비어 있습니다."),
    INVALID_TOKEN_TYPE("INVALID_TOKEN_TYPE", "올바른 토큰 타입이 아닙니다."),
    UNAUTHORIZED("UNAUTHORIZED", "인증이 필요합니다."),
    FORBIDDEN("FORBIDDEN", "접근 권한이 없습니다.");

    private final String code;
    private final String message;
}
