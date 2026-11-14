package com.bank.authorization.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.bank.authorization.dto.JwtResponse;
import com.bank.authorization.dto.LoginRequest;
import com.bank.authorization.entity.Role;
import com.bank.authorization.entity.User;
import com.bank.authorization.exception.InvalidCredentialsException;
import com.bank.authorization.repository.UserRepository;
import com.bank.authorization.security.JwtTokenUtils;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private static final Long PROFILE_ID = 100L;
    private static final String PASSWORD = "secret123";
    private static final String TOKEN = "generated.jwt.token";

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    private PasswordEncoder passwordEncoder;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtTokenUtils);
    }

    private User user() {
        return User.builder()
                .id(1L)
                .profileId(PROFILE_ID)
                .password(passwordEncoder.encode(PASSWORD))
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("login: возвращает JWT при верных учётных данных")
    void loginShouldReturnToken() {
        when(userRepository.findByProfileId(PROFILE_ID)).thenReturn(Optional.of(user()));
        when(jwtTokenUtils.generateToken(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(TOKEN);
        when(jwtTokenUtils.getLifetimeSeconds()).thenReturn(3600L);

        final JwtResponse response = authService.login(new LoginRequest(PROFILE_ID, PASSWORD));

        assertThat(response.getToken()).isEqualTo(TOKEN);
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(response.getProfileId()).isEqualTo(PROFILE_ID);
        assertThat(response.getExpiresInSeconds()).isEqualTo(3600L);
    }

    @Test
    @DisplayName("login: бросает InvalidCredentialsException для неизвестного профиля")
    void loginShouldThrowForUnknownProfile() {
        when(userRepository.findByProfileId(PROFILE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest(PROFILE_ID, PASSWORD)))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("login: бросает InvalidCredentialsException для неверного пароля")
    void loginShouldThrowForWrongPassword() {
        when(userRepository.findByProfileId(PROFILE_ID)).thenReturn(Optional.of(user()));

        assertThatThrownBy(() -> authService.login(new LoginRequest(PROFILE_ID, "wrongPassword")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
