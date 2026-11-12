package com.bank.authorization.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.authorization.dto.JwtResponse;
import com.bank.authorization.dto.LoginRequest;
import com.bank.authorization.entity.Role;
import com.bank.authorization.exception.InvalidCredentialsException;
import com.bank.authorization.security.JwtTokenUtils;
import com.bank.authorization.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthController.class, properties = "server.servlet.context-path=")
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @Test
    @DisplayName("POST /auth: возвращает JWT")
    void shouldReturnToken() throws Exception {
        given(authService.login(any(LoginRequest.class))).willReturn(JwtResponse.builder()
                .token("jwt.token.value")
                .tokenType("Bearer")
                .role(Role.ROLE_USER)
                .profileId(100L)
                .expiresInSeconds(3600L)
                .build());

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"profileId\":100,\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.value"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("POST /auth: возвращает 401 при неверных данных")
    void shouldReturnUnauthorized() throws Exception {
        given(authService.login(any(LoginRequest.class)))
                .willThrow(new InvalidCredentialsException("Invalid profileId or password"));

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"profileId\":100,\"password\":\"wrongPassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /auth: возвращает 400 при пустом теле")
    void shouldReturnBadRequestOnEmptyBody() throws Exception {
        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
