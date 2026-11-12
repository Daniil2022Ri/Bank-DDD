package com.bank.authorization.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.authorization.dto.UserCreateDto;
import com.bank.authorization.dto.UserDto;
import com.bank.authorization.dto.UserUpdateDto;
import com.bank.authorization.entity.Role;
import com.bank.authorization.exception.DuplicateUserException;
import com.bank.authorization.exception.EntityNotFoundException;
import com.bank.authorization.security.JwtTokenUtils;
import com.bank.authorization.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserController.class, properties = "server.servlet.context-path=")
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    private UserDto response() {
        return UserDto.builder()
                .id(1L)
                .profileId(100L)
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("POST /users: регистрирует пользователя")
    void shouldCreateUser() throws Exception {
        given(userService.create(any(UserCreateDto.class))).willReturn(response());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"profileId\":100,\"password\":\"secret123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    @DisplayName("POST /users: возвращает 400 при коротком пароле")
    void shouldRejectShortPassword() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"profileId\":100,\"password\":\"123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("POST /users: возвращает 409 при дубликате профиля")
    void shouldReturnConflictOnDuplicate() throws Exception {
        given(userService.create(any(UserCreateDto.class)))
                .willThrow(new DuplicateUserException("already exists"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"profileId\":100,\"password\":\"secret123\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /users: возвращает список")
    void shouldReturnAllUsers() throws Exception {
        given(userService.getAll()).willReturn(List.of(response()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("GET /users/{id}: возвращает пользователя")
    void shouldReturnUserById() throws Exception {
        given(userService.getById(1L)).willReturn(response());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileId").value(100));
    }

    @Test
    @DisplayName("GET /users/{id}: возвращает 404")
    void shouldReturnNotFound() throws Exception {
        given(userService.getById(99L)).willThrow(new EntityNotFoundException("User with id 99 not found"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /users/{id}: обновляет пользователя")
    void shouldUpdateUser() throws Exception {
        final UserDto admin = response();
        admin.setRole(Role.ROLE_ADMIN);
        given(userService.update(eq(1L), any(UserUpdateDto.class))).willReturn(admin);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserUpdateDto.builder().role(Role.ROLE_ADMIN).build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("DELETE /users/{id}: возвращает 204")
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).delete(1L);
    }

    @Test
    @DisplayName("POST /users: возвращает 400 на битый JSON")
    void shouldReturnBadRequestOnMalformedJson() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"profileId\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed JSON request"));
    }
}
