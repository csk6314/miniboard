package com.miniboard.backend.global.dto;

public record ErrorResponseDto(
        String code,
        String message
) {
}
