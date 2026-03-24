package com.miniboard.backend.global.exception;

import com.miniboard.backend.auth.jwt.JwtAuthenticationException;
import com.miniboard.backend.auth.jwt.JwtErrorCode;
import com.miniboard.backend.global.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleJwtAuthenticationException(JwtAuthenticationException e) {
        JwtErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDto.of(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDto("BAD_REQUEST", e.getMessage()));
    }
}
