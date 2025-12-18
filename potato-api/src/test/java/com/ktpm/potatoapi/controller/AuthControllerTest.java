package com.ktpm.potatoapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean private AuthService authService;
    @Autowired ObjectMapper objectMapper;
    private SignUpRequest signUpRequest;

    @BeforeEach
    public void init() {
        signUpRequest = new SignUpRequest("tanpuh@gmail.com", "Aa@12345", "Phu Thanh");
    }

    @Test
    void signUp_validEmailAndPassword_8chars_success() throws Exception {
        AuthResponse authResponse = new AuthResponse("access-token-123");

        String content = objectMapper.writeValueAsString(signUpRequest);

        when(authService.signUp(ArgumentMatchers.any()))
                .thenReturn(authResponse);

        mockMvc.perform(MockMvcRequestBuilders
                    .post("/auth/sign-up")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(content)
                ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void signUp_validEmailAndPassword_20chars_success() throws Exception {
        AuthResponse authResponse = new AuthResponse("access-token-123");
        signUpRequest.setPassword("Aa@12345678912345678");

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
    void signUp_invalidEmail_incorrectFormat_fail() throws Exception {
        signUpRequest.setEmail("invalid-email");
        String content = objectMapper.writeValueAsString(signUpRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void signUp_invalidEmail_blank_fail() throws Exception {
        signUpRequest.setEmail("");
        String content = objectMapper.writeValueAsString(signUpRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void signUp_invalidPassword_blank_fail() throws Exception {
        signUpRequest.setPassword("");
        String content = objectMapper.writeValueAsString(signUpRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void signUp_invalidPassword_7chars_fail() throws Exception {
        signUpRequest.setPassword("Aa@1234");
        String content = new ObjectMapper().writeValueAsString(signUpRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void signUp_invalidPassword_21chars_fail() throws Exception {
        signUpRequest.setPassword("Aa@123456789123456789");
        String content = new ObjectMapper().writeValueAsString(signUpRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void signUp_invalidPassword_missingUppercase_fail() throws Exception {
        signUpRequest.setPassword("aa@12345");
        String content = new ObjectMapper().writeValueAsString(signUpRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void signUp_invalidPassword_missingLowercase_fail() throws Exception {
        signUpRequest.setPassword("AA@12345");
        String content = new ObjectMapper().writeValueAsString(signUpRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void signUp_invalidPassword_missingSpecial_fail() throws Exception {
        signUpRequest.setPassword("AA123456");
        String content = new ObjectMapper().writeValueAsString(signUpRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void signUp_invalidPassword_missingDigit_fail() throws Exception {
        signUpRequest.setPassword("Aa@!#$%^");
        String content = new ObjectMapper().writeValueAsString(signUpRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
