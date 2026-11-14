package com.bank.history.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.bank.history.dto.HistoryAggregateDto;
import com.bank.history.dto.HistoryDto;
import com.bank.history.entity.History;
import com.bank.history.exception.EntityNotFoundException;
import com.bank.history.mapper.HistoryMapperImpl;
import com.bank.history.repository.HistoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HistoryServiceImplTest {

    private static final Long HISTORY_ID = 1L;
    private static final Long AUDIT_ID = 10L;

    @Mock
    private HistoryRepository historyRepository;

    private HistoryServiceImpl historyService;

    @BeforeEach
    void setUp() {
        historyService = new HistoryServiceImpl(historyRepository, new HistoryMapperImpl());
    }

    private History history(Long transferAuditId, Long profileAuditId, Long accountAuditId) {
        return History.builder()
                .id(HISTORY_ID)
                .transferAuditId(transferAuditId)
                .profileAuditId(profileAuditId)
                .accountAuditId(accountAuditId)
                .build();
    }

    @Test
    @DisplayName("create: сохраняет запись истории")
    void createShouldSaveHistory() {
        when(historyRepository.save(any(History.class))).thenAnswer(invocation -> {
            final History entity = invocation.getArgument(0);
            entity.setId(HISTORY_ID);
            return entity;
        });
        final HistoryDto request = HistoryDto.builder().transferAuditId(AUDIT_ID).build();

        final HistoryDto result = historyService.create(request);

        assertThat(result.getId()).isEqualTo(HISTORY_ID);
        assertThat(result.getTransferAuditId()).isEqualTo(AUDIT_ID);
    }

    @Test
    @DisplayName("getById: возвращает запись")
    void getByIdShouldReturnHistory() {
        when(historyRepository.findById(HISTORY_ID)).thenReturn(Optional.of(history(AUDIT_ID, null, null)));

        final HistoryDto result = historyService.getById(HISTORY_ID);

        assertThat(result.getTransferAuditId()).isEqualTo(AUDIT_ID);
    }

    @Test
    @DisplayName("getById: бросает EntityNotFoundException")
    void getByIdShouldThrowWhenMissing() {
        when(historyRepository.findById(HISTORY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> historyService.getById(HISTORY_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.valueOf(HISTORY_ID));
    }

    @Test
    @DisplayName("getAll: возвращает все записи")
    void getAllShouldReturnAll() {
        when(historyRepository.findAll()).thenReturn(List.of(
                history(AUDIT_ID, null, null), history(null, AUDIT_ID, null)));

        assertThat(historyService.getAll()).hasSize(2);
    }

    @Test
    @DisplayName("getByMicroservice: фильтрует по transfer")
    void getByMicroserviceShouldFilterTransfer() {
        when(historyRepository.findAll()).thenReturn(List.of(
                history(AUDIT_ID, null, null), history(null, AUDIT_ID, null)));

        assertThat(historyService.getByMicroservice("transfer")).hasSize(1);
    }

    @Test
    @DisplayName("getByMicroservice: фильтрует по authorization")
    void getByMicroserviceShouldFilterAuthorization() {
        final History withAuthorization = History.builder()
                .id(HISTORY_ID)
                .authorizationAuditId(AUDIT_ID)
                .build();
        when(historyRepository.findAll()).thenReturn(List.of(withAuthorization, history(AUDIT_ID, null, null)));

        assertThat(historyService.getByMicroservice("authorization")).hasSize(1);
    }

    @Test
    @DisplayName("getByMicroservice: неизвестный сервис вызывает IllegalArgumentException")
    void getByMicroserviceShouldRejectUnknown() {
        assertThatThrownBy(() -> historyService.getByMicroservice("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    @DisplayName("aggregate: считает записи по каждому сервису")
    void aggregateShouldCountEntries() {
        when(historyRepository.findAll()).thenReturn(List.of(
                history(AUDIT_ID, AUDIT_ID, null),
                history(AUDIT_ID, null, AUDIT_ID)));

        final HistoryAggregateDto result = historyService.aggregate();

        assertThat(result.getTotal()).isEqualTo(2);
        assertThat(result.getCounts()).containsEntry("transfer", 2L);
        assertThat(result.getCounts()).containsEntry("profile", 1L);
        assertThat(result.getCounts()).containsEntry("account", 1L);
        assertThat(result.getCounts()).containsEntry("authorization", 0L);
        assertThat(result.getEntries()).hasSize(2);
    }
}
