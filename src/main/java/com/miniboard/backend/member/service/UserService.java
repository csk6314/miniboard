package com.miniboard.backend.member.service;

import com.miniboard.backend.member.domain.UserEntity;
import com.miniboard.backend.member.domain.UserRole;
import com.miniboard.backend.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public Long signUp(String email, String password, String nickname, UserRole userRole) {
        validateDuplicateEmail(email);

        UserEntity user =  new UserEntity(email, password, nickname, userRole);
        userRepository.save(user);

        return user.getId();
    }

    public UserEntity getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    private void validateDuplicateEmail(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
                });
    }

}
