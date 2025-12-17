package com.ktpm.potatoapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.user.dto.AuthResponse;
import com.ktpm.potatoapi.user.dto.SignUpRequest;
import com.ktpm.potatoapi.user.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean private AuthService authService;
    private SignUpRequest signUpRequest;
    @Autowired ObjectMapper objectMapper;

    @BeforeEach
    void init() {
    }

    @Test
    void signUp_validEmailAndPassword_8chars_success() throws Exception {
        signUpRequest = new SignUpRequest("tanpuh@gmail.com", "Aa@12345", "Phu Thanh");
        AuthResponse authResponse = new AuthResponse("access-token-123");

        String content = objectMapper.writeValueAsString(signUpRequest);

        Mockito.when(authService.signUp(ArgumentMatchers.any()))
                .thenReturn(authResponse);

        mockMvc.perform(MockMvcRequestBuilders
                    .post("/auth/sign-up")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(content)
                ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void signUp_validEmailAndPassword_20chars_success() throws Exception {
        signUpRequest = new SignUpRequest("tanpuh@gmail.com", "Aa@12345678912345678", "Phu Thanh");
        AuthResponse authResponse = new AuthResponse("access-token-123");

        String content = objectMapper.writeValueAsString(signUpRequest);

        Mockito.when(authService.signUp(ArgumentMatchers.any()))
                .thenReturn(authResponse);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void signUp_invalidEmail_fail() throws Exception {
        signUpRequest = new SignUpRequest("invalid-email", "Npt@171104", "Phu Thanh");

        String content = objectMapper.writeValueAsString(signUpRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void signUp_invalidPassword_blank_fail() throws Exception {
        signUpRequest = new SignUpRequest("test@gmail.com", "", "Phu Thanh");

        String content = objectMapper.writeValueAsString(signUpRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void signUp_invalidPassword_7chars_fail() throws Exception {
        signUpRequest = new SignUpRequest("test@gmail.com", "Aa@1234", "Phu Thanh");
        String content = new ObjectMapper().writeValueAsString(signUpRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void signUp_invalidPassword_21chars_fail() throws Exception {
        signUpRequest = new SignUpRequest("test@gmail.com", "Aa@123456789123456789", "Phu Thanh");
        String content = new ObjectMapper().writeValueAsString(signUpRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void signUp_userExists_fail() throws Exception {
        signUpRequest = new SignUpRequest("test@gmail.com", "Npt@171104", "Phu Thanh");
        String content = new ObjectMapper().writeValueAsString(signUpRequest);

        Mockito.when(authService.signUp(ArgumentMatchers.any()))
                .thenThrow(new AppException(ErrorCode.USER_EXISTED));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
