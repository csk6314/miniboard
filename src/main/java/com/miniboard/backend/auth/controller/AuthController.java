package com.miniboard.backend.auth.controller;

import com.miniboard.backend.auth.cookie.CookieUtil;
import com.miniboard.backend.auth.dto.*;
import com.miniboard.backend.auth.jwt.JwtTokenProvider;
import com.miniboard.backend.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        TokenResponseDto tokenResponse = authService.login(request);

        ResponseCookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(
                tokenResponse.refreshToken(),
                jwtTokenProvider.getRefreshTokenExpiration()
        );

        LoginResponseDto response = new LoginResponseDto(tokenResponse.accessToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@RequestBody SignUpRequestDto request) {
        Long response = authService.signUp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reissue")
    public ResponseEntity<ReissueResponseDto> reissue(HttpServletRequest request) {
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);

        TokenResponseDto tokenResponseDto = authService.reissue(refreshToken);

        ResponseCookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(
                tokenResponseDto.refreshToken(),
                jwtTokenProvider.getRefreshTokenExpiration()
        );

        ReissueResponseDto response = new ReissueResponseDto(tokenResponseDto.accessToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,refreshTokenCookie.toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);

        authService.logout(refreshToken);

        ResponseCookie deletedCookie = cookieUtil.deleteRefreshTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deletedCookie.toString())
                .build();
    }
}
