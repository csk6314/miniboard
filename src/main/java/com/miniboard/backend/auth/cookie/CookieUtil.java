package com.miniboard.backend.auth.cookie;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public ResponseCookie createRefreshTokenCookie(String refreshToken, long expirationMillis) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // 배포 환경 HTTPS에서는 꼭 true로 변경
                .path("/")
                .sameSite("Lax") // 나중에 도메인 따라 변경해야함
                .build();
    }

    public ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // HTTPS 환경에서는 true
                .path("/")
                .maxAge(0)
                .sameSite("Lax") // 나중에 도메인 따라 변경
                .build();
    }
}
