package com.bank.history;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.history.dto.HistoryDto;
import com.bank.history.repository.HistoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class HistoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HistoryRepository historyRepository;

    @BeforeEach
    void cleanUp() {
        historyRepository.deleteAll();
    }

    private long create(HistoryDto historyDto) throws Exception {
        final MvcResult result = mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(historyDto)))
                .andExpect(status().isCreated())
                .andReturn();
        final JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    @Test
    @DisplayName("Агрегация и выборка по микросервисам")
    void shouldAggregateAndFilter() throws Exception {
        create(HistoryDto.builder().transferAuditId(10L).profileAuditId(20L).build());
        create(HistoryDto.builder().transferAuditId(11L).accountAuditId(30L).build());
        create(HistoryDto.builder().authorizationAuditId(40L).build());

        mockMvc.perform(get("/aggregate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.counts.transfer").value(2))
                .andExpect(jsonPath("$.counts.profile").value(1))
                .andExpect(jsonPath("$.counts.account").value(1))
                .andExpect(jsonPath("$.counts.anti_fraud").value(0))
                .andExpect(jsonPath("$.counts.authorization").value(1));

        mockMvc.perform(get("/transfer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/authorization"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Получение записи по id и 404")
    void shouldReturnHistoryById() throws Exception {
        final long historyId = create(HistoryDto.builder().transferAuditId(10L).build());

        mockMvc.perform(get("/id/{id}", historyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferAuditId").value(10));

        mockMvc.perform(get("/id/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неизвестный микросервис возвращает 400")
    void shouldRejectUnknownMicroservice() throws Exception {
        mockMvc.perform(get("/unknown"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Невалидный id аудита возвращает 400")
    void shouldRejectInvalidAuditId() throws Exception {
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transferAuditId\":-1}"))
                .andExpect(status().isBadRequest());
    }
}
