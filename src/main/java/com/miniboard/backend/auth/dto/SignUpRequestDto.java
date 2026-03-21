package com.miniboard.backend.auth.dto;

public record SignUpRequestDto(
        String email,
        String password,
        String nickname
) {
}
