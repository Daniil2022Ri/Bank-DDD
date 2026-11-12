package com.bank.authorization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.authorization.entity.Audit;
import com.bank.authorization.entity.OperationType;
import com.bank.authorization.entity.Role;
import com.bank.authorization.entity.User;
import com.bank.authorization.repository.AuditRepository;
import com.bank.authorization.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthorizationIntegrationTest {

    private static final long PROFILE_ID = 100L;
    private static final long ADMIN_PROFILE_ID = 900L;
    private static final String PASSWORD = "secret123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanUp() {
        auditRepository.deleteAll();
        userRepository.deleteAll();
    }

    private long register(long profileId) throws Exception {
        final MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"profileId\":%d,\"password\":\"%s\"}", profileId, PASSWORD)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
                .andReturn();
        final JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }

    private String login(long profileId) throws Exception {
        final MvcResult result = mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"profileId\":%d,\"password\":\"%s\"}", profileId, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();
        final JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("token").asText();
    }

    private void createAdmin() {
        userRepository.save(User.builder()
                .profileId(ADMIN_PROFILE_ID)
                .password(passwordEncoder.encode(PASSWORD))
                .role(Role.ROLE_ADMIN)
                .build());
    }

    @Test
    @DisplayName("Регистрация и логин выдают рабочий JWT")
    void shouldRegisterAndLogin() throws Exception {
        final long userId = register(PROFILE_ID);
        final String token = login(PROFILE_ID);

        mockMvc.perform(get("/users/{id}", userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Без токена API возвращает 401")
    void shouldRejectRequestWithoutToken() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Администратор управляет пользователями")
    void adminShouldManageUsers() throws Exception {
        final long userId = register(PROFILE_ID);
        createAdmin();
        final String adminToken = login(ADMIN_PROFILE_ID);

        mockMvc.perform(get("/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(put("/users/{id}", userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"ROLE_ADMIN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));

        mockMvc.perform(delete("/users/{id}", userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Создание и изменение пользователя пишутся в audit")
    void shouldWriteAuditRecords() throws Exception {
        final long userId = register(PROFILE_ID);
        createAdmin();
        final String adminToken = login(ADMIN_PROFILE_ID);

        mockMvc.perform(put("/users/{id}", userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"ROLE_ADMIN\"}"))
                .andExpect(status().isOk());

        final List<Audit> auditRecords = auditRepository.findAll();
        assertThat(auditRecords).hasSize(2);
        assertThat(auditRecords.get(0).getOperationType()).isEqualTo(OperationType.CREATE);
        assertThat(auditRecords.get(0).getCreatedBy()).isEqualTo("system");
        assertThat(auditRecords.get(0).getEntityJson()).contains("\"profileId\":100");
        assertThat(auditRecords.get(1).getOperationType()).isEqualTo(OperationType.UPDATE);
        assertThat(auditRecords.get(1).getCreatedBy()).isEqualTo(String.valueOf(ADMIN_PROFILE_ID));
        assertThat(auditRecords.get(1).getNewEntityJson()).contains("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Дубликат профиля и неверный пароль обрабатываются корректно")
    void shouldHandleConflictsAndBadCredentials() throws Exception {
        register(PROFILE_ID);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"profileId\":%d,\"password\":\"%s\"}", PROFILE_ID, PASSWORD)))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"profileId\":%d,\"password\":\"wrongPassword\"}", PROFILE_ID)))
                .andExpect(status().isUnauthorized());
    }
}
