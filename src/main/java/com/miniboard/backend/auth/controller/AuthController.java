package com.miniboard.backend.auth.controller;

import com.miniboard.backend.auth.dto.LoginRequestDto;
import com.miniboard.backend.auth.dto.SignUpRequestDto;
import com.miniboard.backend.auth.dto.TokenResponseDto;
import com.miniboard.backend.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto request) {
        TokenResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@RequestBody SignUpRequestDto request) {
        Long response = authService.signUp(request);
        return ResponseEntity.ok(response);
    }
}
