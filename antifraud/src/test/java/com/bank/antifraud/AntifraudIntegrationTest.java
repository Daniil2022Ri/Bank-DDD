package com.bank.antifraud;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.antifraud.entity.Audit;
import com.bank.antifraud.entity.OperationType;
import com.bank.antifraud.repository.AuditRepository;
import com.bank.antifraud.repository.SuspiciousAccountTransferRepository;
import com.bank.antifraud.repository.SuspiciousCardTransferRepository;
import com.bank.antifraud.repository.SuspiciousPhoneTransferRepository;
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
class AntifraudIntegrationTest {

    private static final String CARD_BODY = """
            {
              "transferId": 100,
              "blocked": false,
              "suspicious": true,
              "suspiciousReason": "Превышен лимит операций"
            }
            """;

    private static final String UPDATE_BODY = """
            {
              "transferId": 100,
              "blocked": true,
              "suspicious": true,
              "blockedReason": "Подозрительная активность",
              "suspiciousReason": "Превышен лимит операций"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SuspiciousCardTransferRepository cardTransferRepository;

    @Autowired
    private SuspiciousPhoneTransferRepository phoneTransferRepository;

    @Autowired
    private SuspiciousAccountTransferRepository accountTransferRepository;

    @Autowired
    private AuditRepository auditRepository;

    @BeforeEach
    void cleanUp() {
        auditRepository.deleteAll();
        cardTransferRepository.deleteAll();
        phoneTransferRepository.deleteAll();
        accountTransferRepository.deleteAll();
    }

    private long createCard() throws Exception {
        final MvcResult result = mockMvc.perform(post("/suspicious-transfers/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CARD_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.suspicious").value(true))
                .andReturn();
        final JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }

    @Test
    @DisplayName("Полный CRUD-сценарий карточного перевода")
    void shouldPerformCardCrudFlow() throws Exception {
        final long transferId = createCard();

        mockMvc.perform(get("/suspicious-transfers/cards/{id}", transferId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferId").value(100));

        mockMvc.perform(get("/suspicious-transfers/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(put("/suspicious-transfers/cards/{id}", transferId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blocked").value(true))
                .andExpect(jsonPath("$.blockedReason").value("Подозрительная активность"));

        mockMvc.perform(delete("/suspicious-transfers/cards/{id}", transferId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/suspicious-transfers/cards/{id}", transferId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Создание телефонного и счётового переводов")
    void shouldCreatePhoneAndAccountTransfers() throws Exception {
        mockMvc.perform(post("/suspicious-transfers/phones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CARD_BODY.replace("100", "200")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transferId").value(200));

        mockMvc.perform(post("/suspicious-transfers/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CARD_BODY.replace("100", "300")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transferId").value(300));

        mockMvc.perform(get("/suspicious-transfers/phones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/suspicious-transfers/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Создание и изменение перевода пишутся в audit")
    void shouldWriteAuditRecords() throws Exception {
        final long transferId = createCard();

        mockMvc.perform(put("/suspicious-transfers/cards/{id}", transferId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isOk());

        final List<Audit> auditRecords = auditRepository.findAll();
        assertThat(auditRecords).hasSize(2);
        assertThat(auditRecords.get(0).getOperationType()).isEqualTo(OperationType.CREATE);
        assertThat(auditRecords.get(0).getEntityType().name()).isEqualTo("CARD_TRANSFER");
        assertThat(auditRecords.get(0).getEntityJson()).contains("\"transferId\":100");
        assertThat(auditRecords.get(1).getOperationType()).isEqualTo(OperationType.UPDATE);
        assertThat(auditRecords.get(1).getEntityJson()).contains("\"blocked\":false");
        assertThat(auditRecords.get(1).getNewEntityJson()).contains("\"blocked\":true");
    }

    @Test
    @DisplayName("Дубликат transferId возвращает 409")
    void shouldRejectDuplicateTransfer() throws Exception {
        createCard();

        mockMvc.perform(post("/suspicious-transfers/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CARD_BODY))
                .andExpect(status().isConflict());
    }
}
