package com.bank.authorization.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.authorization.dto.UserCreateDto;
import com.bank.authorization.dto.UserDto;
import com.bank.authorization.dto.UserUpdateDto;
import com.bank.authorization.entity.Role;
import com.bank.authorization.entity.User;
import com.bank.authorization.exception.DuplicateUserException;
import com.bank.authorization.exception.EntityNotFoundException;
import com.bank.authorization.mapper.UserMapperImpl;
import com.bank.authorization.repository.UserRepository;
import java.util.List;
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
class UserServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long PROFILE_ID = 100L;
    private static final String RAW_PASSWORD = "secret123";

    @Mock
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserServiceImpl(userRepository, new UserMapperImpl(), passwordEncoder);
    }

    private UserCreateDto createDto() {
        return UserCreateDto.builder()
                .profileId(PROFILE_ID)
                .password(RAW_PASSWORD)
                .build();
    }

    private User user() {
        return User.builder()
                .id(USER_ID)
                .profileId(PROFILE_ID)
                .password(passwordEncoder.encode(RAW_PASSWORD))
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("create: сохраняет пользователя с ролью ROLE_USER и хешем пароля")
    void createShouldSaveUser() {
        when(userRepository.existsByProfileId(PROFILE_ID)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            final User entity = invocation.getArgument(0);
            entity.setId(USER_ID);
            return entity;
        });

        final UserDto result = userService.create(createDto());

        assertThat(result.getId()).isEqualTo(USER_ID);
        assertThat(result.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(result.getProfileId()).isEqualTo(PROFILE_ID);
    }

    @Test
    @DisplayName("create: хеширует пароль перед сохранением")
    void createShouldHashPassword() {
        when(userRepository.existsByProfileId(PROFILE_ID)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.create(createDto());

        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(
                user -> !RAW_PASSWORD.equals(user.getPassword())
                        && passwordEncoder.matches(RAW_PASSWORD, user.getPassword())));
    }

    @Test
    @DisplayName("create: запрещает дубликат profileId")
    void createShouldRejectDuplicateProfileId() {
        when(userRepository.existsByProfileId(PROFILE_ID)).thenReturn(true);

        assertThatThrownBy(() -> userService.create(createDto()))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessageContaining(String.valueOf(PROFILE_ID));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("getById: возвращает пользователя")
    void getByIdShouldReturnUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user()));

        final UserDto result = userService.getById(USER_ID);

        assertThat(result.getId()).isEqualTo(USER_ID);
        assertThat(result.getRole()).isEqualTo(Role.ROLE_USER);
    }

    @Test
    @DisplayName("getById: бросает EntityNotFoundException")
    void getByIdShouldThrowWhenMissing() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(USER_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.valueOf(USER_ID));
    }

    @Test
    @DisplayName("getAll: возвращает список пользователей")
    void getAllShouldReturnUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user()));

        final List<UserDto> result = userService.getAll();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("update: меняет пароль")
    void updateShouldChangePassword() {
        final User existing = user();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        final UserUpdateDto updateDto = UserUpdateDto.builder().password("newPassword1").build();
        userService.update(USER_ID, updateDto);

        assertThat(passwordEncoder.matches("newPassword1", existing.getPassword())).isTrue();
    }

    @Test
    @DisplayName("update: меняет роль")
    void updateShouldChangeRole() {
        final User existing = user();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        final UserUpdateDto updateDto = UserUpdateDto.builder().role(Role.ROLE_ADMIN).build();
        final UserDto result = userService.update(USER_ID, updateDto);

        assertThat(result.getRole()).isEqualTo(Role.ROLE_ADMIN);
    }

    @Test
    @DisplayName("update: бросает EntityNotFoundException")
    void updateShouldThrowWhenMissing() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(USER_ID, UserUpdateDto.builder().build()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("delete: удаляет пользователя")
    void deleteShouldRemoveUser() {
        final User existing = user();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existing));

        userService.delete(USER_ID);

        verify(userRepository).delete(existing);
    }

    @Test
    @DisplayName("delete: бросает EntityNotFoundException")
    void deleteShouldThrowWhenMissing() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(USER_ID))
                .isInstanceOf(EntityNotFoundException.class);
        verify(userRepository, never()).delete(any(User.class));
    }
}
