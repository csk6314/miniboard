package com.miniboard.backend.auth.dto;

public record LoginRequestDto(
        String email,
        String password
) {
}
