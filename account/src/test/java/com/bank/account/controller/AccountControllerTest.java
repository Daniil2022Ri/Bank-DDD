package com.bank.account.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.account.dto.AccountDto;
import com.bank.account.exception.AccountAlreadyExistsException;
import com.bank.account.exception.EntityNotFoundException;
import com.bank.account.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AccountController.class, properties = "server.servlet.context-path=")
@ActiveProfiles("test")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    private AccountDto request() {
        return AccountDto.builder()
                .passportId(11L)
                .accountNumber(4081781000000000001L)
                .bankDetailsId(22L)
                .money(new BigDecimal("150.00"))
                .profileId(33L)
                .build();
    }

    private AccountDto response() {
        final AccountDto response = request();
        response.setId(1L);
        response.setNegativeBalance(false);
        return response;
    }

    @Test
    @DisplayName("POST /accounts: создаёт счёт и возвращает 201")
    void shouldCreateAccount() throws Exception {
        given(accountService.create(any(AccountDto.class))).willReturn(response());

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountNumber").value(4081781000000000001L));
    }

    @Test
    @DisplayName("POST /accounts: возвращает 400 при невалидном теле")
    void shouldReturnBadRequestOnInvalidBody() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("POST /accounts: возвращает 400 на битый JSON")
    void shouldReturnBadRequestOnMalformedJson() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed JSON request"));
    }

    @Test
    @DisplayName("POST /accounts: возвращает 409 при дубликате")
    void shouldReturnConflictOnDuplicate() throws Exception {
        given(accountService.create(any(AccountDto.class)))
                .willThrow(new AccountAlreadyExistsException("Account already exists"));

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /accounts/{id}: возвращает счёт")
    void shouldReturnAccountById() throws Exception {
        given(accountService.getById(1L)).willReturn(response());

        mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.profileId").value(33));
    }

    @Test
    @DisplayName("GET /accounts/{id}: возвращает 404, если счёта нет")
    void shouldReturnNotFound() throws Exception {
        given(accountService.getById(99L)).willThrow(new EntityNotFoundException("Account with id 99 not found"));

        mockMvc.perform(get("/accounts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /accounts: возвращает список счетов")
    void shouldReturnAllAccounts() throws Exception {
        given(accountService.getAll()).willReturn(List.of(response()));

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("PUT /accounts/{id}: обновляет счёт")
    void shouldUpdateAccount() throws Exception {
        given(accountService.update(eq(1L), any(AccountDto.class))).willReturn(response());

        mockMvc.perform(put("/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.money").value(150.00));
    }

    @Test
    @DisplayName("DELETE /accounts/{id}: удаляет счёт и возвращает 204")
    void shouldDeleteAccount() throws Exception {
        mockMvc.perform(delete("/accounts/1"))
                .andExpect(status().isNoContent());

        verify(accountService).delete(1L);
    }

    @Test
    @DisplayName("DELETE /accounts/{id}: возвращает 404, если счёта нет")
    void shouldReturnNotFoundOnDelete() throws Exception {
        doThrow(new EntityNotFoundException("Account with id 99 not found")).when(accountService).delete(99L);

        mockMvc.perform(delete("/accounts/99"))
                .andExpect(status().isNotFound());
    }
}
