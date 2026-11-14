package com.bank.history.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.history.dto.HistoryAggregateDto;
import com.bank.history.dto.HistoryDto;
import com.bank.history.exception.EntityNotFoundException;
import com.bank.history.service.HistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = HistoryController.class, properties = "server.servlet.context-path=")
@ActiveProfiles("test")
class HistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HistoryService historyService;

    private HistoryDto historyDto() {
        return HistoryDto.builder()
                .id(1L)
                .transferAuditId(10L)
                .build();
    }

    @Test
    @DisplayName("POST /: создаёт запись истории")
    void shouldCreateHistory() throws Exception {
        given(historyService.create(any(HistoryDto.class))).willReturn(historyDto());

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(historyDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /: возвращает список")
    void shouldReturnAllHistory() throws Exception {
        given(historyService.getAll()).willReturn(List.of(historyDto()));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transferAuditId").value(10));
    }

    @Test
    @DisplayName("GET /aggregate: возвращает агрегацию")
    void shouldReturnAggregate() throws Exception {
        given(historyService.aggregate()).willReturn(HistoryAggregateDto.builder()
                .total(1L)
                .counts(Map.of("transfer", 1L))
                .entries(List.of(historyDto()))
                .build());

        mockMvc.perform(get("/aggregate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.counts.transfer").value(1));
    }

    @Test
    @DisplayName("GET /{microservice}: возвращает историю сервиса")
    void shouldReturnHistoryByMicroservice() throws Exception {
        given(historyService.getByMicroservice("transfer")).willReturn(List.of(historyDto()));

        mockMvc.perform(get("/transfer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /{microservice}: неизвестный сервис → 400")
    void shouldReturnBadRequestForUnknownMicroservice() throws Exception {
        given(historyService.getByMicroservice("unknown"))
                .willThrow(new IllegalArgumentException("Unknown microservice: unknown"));

        mockMvc.perform(get("/unknown"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /id/{id}: возвращает запись")
    void shouldReturnHistoryById() throws Exception {
        given(historyService.getById(1L)).willReturn(historyDto());

        mockMvc.perform(get("/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /id/{id}: возвращает 404")
    void shouldReturnNotFound() throws Exception {
        given(historyService.getById(99L))
                .willThrow(new EntityNotFoundException("History entry with id 99 not found"));

        mockMvc.perform(get("/id/99"))
                .andExpect(status().isNotFound());
    }
}
