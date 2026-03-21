package com.miniboard.backend.auth.service;

import com.miniboard.backend.auth.dto.LoginRequestDto;
import com.miniboard.backend.auth.dto.SignUpRequestDto;
import com.miniboard.backend.auth.dto.TokenResponseDto;
import com.miniboard.backend.auth.jwt.JwtTokenProvider;
import com.miniboard.backend.auth.repository.RefreshTokenRepository;
import com.miniboard.backend.member.domain.UserEntity;
import com.miniboard.backend.member.domain.UserRole;
import com.miniboard.backend.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenResponseDto login(LoginRequestDto request) {
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        TokenResponseDto tokenResponse = jwtTokenProvider.generateTokens(user.getId());

        refreshTokenRepository.save(
                user.getId(),
                tokenResponse.refreshToken(),
                jwtTokenProvider.getRefreshTokenExpiration()
        );

        return tokenResponse;
    }

    @Transactional
    public Long signUp(SignUpRequestDto request) {
        validateDuplicateEmail(request.email());

        String encodedPassword = passwordEncoder.encode(request.password());

        UserEntity user =  new UserEntity(
                request.email(),
                encodedPassword,
                request.nickname(),
                UserRole.USER
        );

        userRepository.save(user);

        return user.getId();
    }

    private void validateDuplicateEmail(String email) {
        if(userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }
    }

}
