package com.miniboard.backend.global.dto;

public record ErrorResponseDto(
        String code,
        String message
) {
    public static ErrorResponseDto of(String code, String message) {
        return new ErrorResponseDto(code, message);
    }
}
