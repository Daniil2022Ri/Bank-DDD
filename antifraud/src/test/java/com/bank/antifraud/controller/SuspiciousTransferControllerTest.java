package com.bank.antifraud.controller;

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

import com.bank.antifraud.dto.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.SuspiciousPhoneTransferDto;
import com.bank.antifraud.exception.DuplicateTransferException;
import com.bank.antifraud.exception.EntityNotFoundException;
import com.bank.antifraud.exception.TransferValidationException;
import com.bank.antifraud.service.SuspiciousTransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SuspiciousTransferController.class, properties = "server.servlet.context-path=")
@ActiveProfiles("test")
class SuspiciousTransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SuspiciousTransferService suspiciousTransferService;

    private SuspiciousCardTransferDto cardDto() {
        return SuspiciousCardTransferDto.builder()
                .transferId(100L)
                .blocked(false)
                .suspicious(true)
                .suspiciousReason("Подозрительная операция")
                .build();
    }

    private SuspiciousCardTransferDto cardResponse() {
        final SuspiciousCardTransferDto response = cardDto();
        response.setId(1L);
        return response;
    }

    @Test
    @DisplayName("POST /cards: создаёт карточный перевод")
    void shouldCreateCardTransfer() throws Exception {
        given(suspiciousTransferService.createCard(any(SuspiciousCardTransferDto.class))).willReturn(cardResponse());

        mockMvc.perform(post("/suspicious-transfers/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.transferId").value(100));
    }

    @Test
    @DisplayName("POST /cards: возвращает 400 при невалидном теле")
    void shouldReturnBadRequestOnInvalidBody() throws Exception {
        mockMvc.perform(post("/suspicious-transfers/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transferId\": 100}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("POST /cards: возвращает 400 при отсутствии причины блокировки")
    void shouldReturnBadRequestOnMissingBlockedReason() throws Exception {
        given(suspiciousTransferService.createCard(any(SuspiciousCardTransferDto.class)))
                .willThrow(new TransferValidationException("blockedReason is required"));

        mockMvc.perform(post("/suspicious-transfers/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /cards: возвращает 409 при дубликате")
    void shouldReturnConflictOnDuplicate() throws Exception {
        given(suspiciousTransferService.createCard(any(SuspiciousCardTransferDto.class)))
                .willThrow(new DuplicateTransferException("already exists"));

        mockMvc.perform(post("/suspicious-transfers/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto())))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /cards: возвращает список")
    void shouldReturnAllCards() throws Exception {
        given(suspiciousTransferService.getAllCards()).willReturn(List.of(cardResponse()));

        mockMvc.perform(get("/suspicious-transfers/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("GET /cards/{id}: возвращает перевод")
    void shouldReturnCardById() throws Exception {
        given(suspiciousTransferService.getCardById(1L)).willReturn(cardResponse());

        mockMvc.perform(get("/suspicious-transfers/cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferId").value(100));
    }

    @Test
    @DisplayName("GET /cards/{id}: возвращает 404")
    void shouldReturnNotFound() throws Exception {
        given(suspiciousTransferService.getCardById(99L))
                .willThrow(new EntityNotFoundException("card transfer 99 not found"));

        mockMvc.perform(get("/suspicious-transfers/cards/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /cards/{id}: обновляет перевод")
    void shouldUpdateCardTransfer() throws Exception {
        given(suspiciousTransferService.updateCard(eq(1L), any(SuspiciousCardTransferDto.class)))
                .willReturn(cardResponse());

        mockMvc.perform(put("/suspicious-transfers/cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suspicious").value(true));
    }

    @Test
    @DisplayName("DELETE /cards/{id}: возвращает 204")
    void shouldDeleteCardTransfer() throws Exception {
        mockMvc.perform(delete("/suspicious-transfers/cards/1"))
                .andExpect(status().isNoContent());

        verify(suspiciousTransferService).deleteCard(1L);
    }

    @Test
    @DisplayName("POST /phones: создаёт телефонный перевод")
    void shouldCreatePhoneTransfer() throws Exception {
        final SuspiciousPhoneTransferDto response = SuspiciousPhoneTransferDto.builder()
                .id(2L)
                .transferId(200L)
                .blocked(true)
                .suspicious(true)
                .blockedReason("Блокировка")
                .suspiciousReason("Подозрительно")
                .build();
        given(suspiciousTransferService.createPhone(any(SuspiciousPhoneTransferDto.class))).willReturn(response);

        mockMvc.perform(post("/suspicious-transfers/phones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    @DisplayName("POST /accounts: создаёт перевод по счёту")
    void shouldCreateAccountTransfer() throws Exception {
        final SuspiciousAccountTransferDto response = SuspiciousAccountTransferDto.builder()
                .id(3L)
                .transferId(300L)
                .blocked(false)
                .suspicious(true)
                .suspiciousReason("Подозрительно")
                .build();
        given(suspiciousTransferService.createAccount(any(SuspiciousAccountTransferDto.class))).willReturn(response);

        mockMvc.perform(post("/suspicious-transfers/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    @DisplayName("POST /cards: возвращает 400 на битый JSON")
    void shouldReturnBadRequestOnMalformedJson() throws Exception {
        mockMvc.perform(post("/suspicious-transfers/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transferId\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed JSON request"));
    }
}
