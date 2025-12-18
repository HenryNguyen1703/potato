package com.ktpm.potatoapi.service;

import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.user.dto.SignUpRequest;
import com.ktpm.potatoapi.user.entity.Role;
import com.ktpm.potatoapi.user.entity.User;
import com.ktpm.potatoapi.user.repo.UserRepository;
import com.ktpm.potatoapi.user.service.AuthService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthServiceTest {
    @Autowired private AuthService authService;
    @MockitoBean private UserRepository userRepository;
    private SignUpRequest signUpRequest;
    private User user;

    @BeforeEach
    public void init() {
        signUpRequest = new SignUpRequest("tanpuh@gmail.com", "Aa@12345", "Phu Thanh");
        user = User.builder()
                .id(1L)
                .fullName("Phu Thanh")
                .email("tanpuh@gmail.com")
                .role(Role.CUSTOMER)
                .build();
    }

    @Test
    void signUp_valid_success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        var response = authService.signUp(signUpRequest);

        Assertions.assertThat(response.getToken()).isNotBlank();
    }

    @Test
    void signUp_userExisted_fail() {
        when(userRepository.save(any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);

        var exception = org.junit.jupiter.api.Assertions
                .assertThrows(AppException.class, () -> authService.signUp(signUpRequest));

        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(3001);
    }
}
