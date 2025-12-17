package com.ktpm.potatoapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktpm.potatoapi.user.controller.AuthController;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
    private AuthResponse authResponse;

    @BeforeEach
    void initData() {
        signUpRequest = new SignUpRequest("tanpuh@gmail.com", "Npt@171104", "Phu Thanh");
        authResponse = new AuthResponse("access-token-123");
    }

    @Test
    void signUp_success() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(signUpRequest);

        // when - then
        Mockito.when(authService.signUp(ArgumentMatchers.any())).thenReturn(authResponse);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }
}