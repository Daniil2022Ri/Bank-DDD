package com.bank.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.account.entity.Audit;
import com.bank.account.entity.OperationType;
import com.bank.account.repository.AccountRepository;
import com.bank.account.repository.AuditRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountIntegrationTest {

    private static final String ACCOUNT_BODY = """
            {
              "passportId": 11,
              "accountNumber": 4081781000000000001,
              "bankDetailsId": 22,
              "money": 150.00,
              "profileId": 33
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuditRepository auditRepository;

    @BeforeEach
    void cleanUp() {
        auditRepository.deleteAll();
        accountRepository.deleteAll();
    }

    private long createAccount() throws Exception {
        final MvcResult result = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ACCOUNT_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.negativeBalance").value(false))
                .andReturn();
        final JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }

    @Test
    @DisplayName("Полный CRUD-сценарий через REST API")
    void shouldPerformFullCrudFlow() throws Exception {
        final long accountId = createAccount();

        mockMvc.perform(get("/accounts/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(4081781000000000001L));

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        final String updateBody = ACCOUNT_BODY.replace("150.00", "-25.50");
        mockMvc.perform(put("/accounts/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.money").value(-25.50))
                .andExpect(jsonPath("$.negativeBalance").value(true));

        mockMvc.perform(delete("/accounts/{id}", accountId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/accounts/{id}", accountId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Создание счёта с существующим номером возвращает 409")
    void shouldRejectDuplicateAccountNumber() throws Exception {
        createAccount();

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ACCOUNT_BODY))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Создание и изменение счёта пишутся в audit")
    void shouldWriteAuditRecords() throws Exception {
        final long accountId = createAccount();

        mockMvc.perform(put("/accounts/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ACCOUNT_BODY.replace("150.00", "200.00")))
                .andExpect(status().isOk());

        final List<Audit> auditRecords = auditRepository.findAll();
        assertThat(auditRecords).hasSize(2);
        assertThat(auditRecords.get(0).getOperationType()).isEqualTo(OperationType.CREATE);
        assertThat(auditRecords.get(0).getCreatedBy()).isEqualTo("system");
        assertThat(auditRecords.get(0).getEntityJson()).contains("4081781000000000001");
        assertThat(auditRecords.get(1).getOperationType()).isEqualTo(OperationType.UPDATE);
        assertThat(auditRecords.get(1).getEntityJson()).contains("150.00");
        assertThat(auditRecords.get(1).getNewEntityJson()).contains("200.00");
    }

    @Test
    @DisplayName("Невалидный запрос возвращает 400")
    void shouldRejectInvalidRequest() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"passportId\": -1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
