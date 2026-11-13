package com.bank.transfer.controller;

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

import com.bank.transfer.dto.AccountTransferDto;
import com.bank.transfer.dto.CardTransferDto;
import com.bank.transfer.dto.PhoneTransferDto;
import com.bank.transfer.exception.DuplicateTransferException;
import com.bank.transfer.exception.EntityNotFoundException;
import com.bank.transfer.service.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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

@WebMvcTest(controllers = TransferController.class, properties = "server.servlet.context-path=")
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransferService transferService;

    private AccountTransferDto accountDto() {
        return AccountTransferDto.builder()
                .id(1L)
                .accountNumber(4081781000000000001L)
                .amount(new BigDecimal("150.00"))
                .purpose("Перевод другу")
                .accountDetailsId(55L)
                .build();
    }

    @Test
    @DisplayName("POST /account: создаёт перевод по счёту")
    void shouldCreateAccountTransfer() throws Exception {
        given(transferService.createAccount(any(AccountTransferDto.class))).willReturn(accountDto());

        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(150.00));
    }

    @Test
    @DisplayName("POST /account: возвращает 409 при дубликате")
    void shouldReturnConflictOnDuplicate() throws Exception {
        given(transferService.createAccount(any(AccountTransferDto.class)))
                .willThrow(new DuplicateTransferException("already exists"));

        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto())))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /account: возвращает 400 при отрицательной сумме")
    void shouldRejectNegativeAmount() throws Exception {
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":1,\"amount\":-5,\"accountDetailsId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("GET /account: возвращает список переводов")
    void shouldReturnAllAccountTransfers() throws Exception {
        given(transferService.getAllAccounts()).willReturn(List.of(accountDto()));

        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value(4081781000000000001L));
    }

    @Test
    @DisplayName("GET /account/{id}: возвращает 404")
    void shouldReturnNotFound() throws Exception {
        given(transferService.getAccountById(99L))
                .willThrow(new EntityNotFoundException("Account transfer with id 99 not found"));

        mockMvc.perform(get("/account/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /card/{id}: обновляет перевод")
    void shouldUpdateCardTransfer() throws Exception {
        final CardTransferDto response = CardTransferDto.builder()
                .id(2L)
                .cardNumber(4276000000000001L)
                .amount(new BigDecimal("50.00"))
                .accountDetailsId(55L)
                .build();
        given(transferService.updateCard(eq(2L), any(CardTransferDto.class))).willReturn(response);

        mockMvc.perform(put("/card/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value(4276000000000001L));
    }

    @Test
    @DisplayName("POST /phone: создаёт перевод по телефону")
    void shouldCreatePhoneTransfer() throws Exception {
        final PhoneTransferDto response = PhoneTransferDto.builder()
                .id(3L)
                .phoneNumber(79990000000L)
                .amount(new BigDecimal("10.00"))
                .accountDetailsId(55L)
                .build();
        given(transferService.createPhone(any(PhoneTransferDto.class))).willReturn(response);

        mockMvc.perform(post("/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.phoneNumber").value(79990000000L));
    }

    @Test
    @DisplayName("DELETE /phone/{id}: возвращает 204")
    void shouldDeletePhoneTransfer() throws Exception {
        mockMvc.perform(delete("/phone/3"))
                .andExpect(status().isNoContent());

        verify(transferService).deletePhone(3L);
    }

    @Test
    @DisplayName("POST /account: возвращает 400 на битый JSON")
    void shouldReturnBadRequestOnMalformedJson() throws Exception {
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed JSON request"));
    }
}
