package com.bank.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.transfer.dto.AccountTransferDto;
import com.bank.transfer.dto.CardTransferDto;
import com.bank.transfer.dto.PhoneTransferDto;
import com.bank.transfer.entity.Audit;
import com.bank.transfer.entity.EntityType;
import com.bank.transfer.entity.OperationType;
import com.bank.transfer.repository.AccountTransferRepository;
import com.bank.transfer.repository.AuditRepository;
import com.bank.transfer.repository.CardTransferRepository;
import com.bank.transfer.repository.PhoneTransferRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
class TransferIntegrationTest {

    private static final Long ACCOUNT_NUMBER = 4081781000000000001L;
    private static final Long CARD_NUMBER = 4276000000000001L;
    private static final Long PHONE_NUMBER = 79990000000L;
    private static final Long ACCOUNT_DETAILS_ID = 55L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private AccountTransferRepository accountTransferRepository;

    @Autowired
    private CardTransferRepository cardTransferRepository;

    @Autowired
    private PhoneTransferRepository phoneTransferRepository;

    @BeforeEach
    void cleanUp() {
        auditRepository.deleteAll();
        accountTransferRepository.deleteAll();
        cardTransferRepository.deleteAll();
        phoneTransferRepository.deleteAll();
    }

    private long create(String path, Object body) throws Exception {
        final MvcResult result = mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();
        final JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    private AccountTransferDto accountDto() {
        return AccountTransferDto.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .amount(new BigDecimal("150.00"))
                .purpose("Перевод другу")
                .accountDetailsId(ACCOUNT_DETAILS_ID)
                .build();
    }

    @Test
    @DisplayName("Все три вида переводов создаются и пишутся в audit")
    void shouldCreateAllTransferTypes() throws Exception {
        final long accountId = create("/account", accountDto());
        create("/card", CardTransferDto.builder()
                .cardNumber(CARD_NUMBER)
                .amount(new BigDecimal("75.50"))
                .accountDetailsId(ACCOUNT_DETAILS_ID)
                .build());
        create("/phone", PhoneTransferDto.builder()
                .phoneNumber(PHONE_NUMBER)
                .amount(new BigDecimal("10.00"))
                .accountDetailsId(ACCOUNT_DETAILS_ID)
                .build());

        mockMvc.perform(get("/account/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(ACCOUNT_NUMBER));

        final List<Audit> auditRecords = auditRepository.findAll();
        assertThat(auditRecords).hasSize(3);
        assertThat(auditRecords).extracting(Audit::getEntityType)
                .containsExactlyInAnyOrder(EntityType.ACCOUNT_TRANSFER, EntityType.CARD_TRANSFER,
                        EntityType.PHONE_TRANSFER);
        assertThat(auditRecords).extracting(Audit::getOperationType)
                .containsOnly(OperationType.CREATE);
    }

    @Test
    @DisplayName("Обновление перевода пишет старые и новые данные в audit")
    void shouldWriteAuditOnUpdate() throws Exception {
        final long accountId = create("/account", accountDto());

        final AccountTransferDto update = accountDto();
        update.setAmount(new BigDecimal("250.00"));
        update.setPurpose("Возврат долга");
        mockMvc.perform(put("/account/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(250.00));

        final List<Audit> auditRecords = auditRepository.findAll();
        assertThat(auditRecords).hasSize(2);
        assertThat(auditRecords.get(1).getOperationType()).isEqualTo(OperationType.UPDATE);
        assertThat(auditRecords.get(1).getEntityJson()).contains("150.00");
        assertThat(auditRecords.get(1).getNewEntityJson()).contains("250.00");
    }

    @Test
    @DisplayName("Дубликат номера счёта возвращает 409")
    void shouldRejectDuplicateAccountNumber() throws Exception {
        create("/account", accountDto());

        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto())))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Удаление перевода и список")
    void shouldDeleteTransfer() throws Exception {
        final long accountId = create("/account", accountDto());
        create("/card", CardTransferDto.builder()
                .cardNumber(CARD_NUMBER)
                .amount(new BigDecimal("5.00"))
                .accountDetailsId(ACCOUNT_DETAILS_ID)
                .build());

        mockMvc.perform(get("/card"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(delete("/account/{id}", accountId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/account/{id}", accountId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Невалидная сумма возвращает 400")
    void shouldRejectInvalidAmount() throws Exception {
        mockMvc.perform(post("/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\":79990000000,\"amount\":0,\"accountDetailsId\":55}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
