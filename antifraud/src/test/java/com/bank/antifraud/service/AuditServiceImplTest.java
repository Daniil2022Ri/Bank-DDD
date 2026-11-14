package com.bank.antifraud.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.entity.Audit;
import com.bank.antifraud.entity.EntityType;
import com.bank.antifraud.entity.OperationType;
import com.bank.antifraud.entity.SuspiciousCardTransfer;
import com.bank.antifraud.exception.EntityNotFoundException;
import com.bank.antifraud.mapper.SuspiciousTransferMapperImpl;
import com.bank.antifraud.repository.AuditRepository;
import com.bank.antifraud.repository.SuspiciousAccountTransferRepository;
import com.bank.antifraud.repository.SuspiciousCardTransferRepository;
import com.bank.antifraud.repository.SuspiciousPhoneTransferRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

    private static final Long CARD_ID = 1L;
    private static final Long TRANSFER_ID = 100L;

    @Mock
    private AuditRepository auditRepository;

    @Mock
    private SuspiciousCardTransferRepository cardTransferRepository;

    @Mock
    private SuspiciousPhoneTransferRepository phoneTransferRepository;

    @Mock
    private SuspiciousAccountTransferRepository accountTransferRepository;

    private AuditServiceImpl auditService;

    @BeforeEach
    void setUp() {
        auditService = new AuditServiceImpl(auditRepository, cardTransferRepository, phoneTransferRepository,
                accountTransferRepository, new SuspiciousTransferMapperImpl(), new ObjectMapper());
    }

    private SuspiciousCardTransferDto cardDto() {
        return SuspiciousCardTransferDto.builder()
                .id(CARD_ID)
                .transferId(TRANSFER_ID)
                .blocked(true)
                .suspicious(true)
                .blockedReason("Блокировка")
                .suspiciousReason("Подозрительно")
                .build();
    }

    @Test
    @DisplayName("logCreate: пишет аудит операции CREATE")
    void shouldLogCreate() {
        final ArgumentCaptor<Audit> captor = ArgumentCaptor.forClass(Audit.class);

        auditService.logCreate(EntityType.CARD_TRANSFER, cardDto());

        verify(auditRepository).save(captor.capture());
        final Audit audit = captor.getValue();
        assertThat(audit.getEntityType()).isEqualTo(EntityType.CARD_TRANSFER);
        assertThat(audit.getOperationType()).isEqualTo(OperationType.CREATE);
        assertThat(audit.getCreatedBy()).isEqualTo("system");
        assertThat(audit.getEntityJson()).contains("\"transferId\":100");
    }

    @Test
    @DisplayName("logUpdate: пишет старое и новое состояние")
    void shouldLogUpdate() {
        final ArgumentCaptor<Audit> captor = ArgumentCaptor.forClass(Audit.class);

        auditService.logUpdate(EntityType.CARD_TRANSFER, "{\"blocked\":false}", cardDto());

        verify(auditRepository).save(captor.capture());
        final Audit audit = captor.getValue();
        assertThat(audit.getOperationType()).isEqualTo(OperationType.UPDATE);
        assertThat(audit.getEntityJson()).isEqualTo("{\"blocked\":false}");
        assertThat(audit.getNewEntityJson()).contains("\"transferId\":100");
        assertThat(audit.getModifiedAt()).isNotNull();
    }

    @Test
    @DisplayName("currentStateJson: сериализует текущее состояние перевода")
    void shouldReturnCurrentStateJson() {
        when(cardTransferRepository.findById(CARD_ID)).thenReturn(Optional.of(SuspiciousCardTransfer.builder()
                .id(CARD_ID)
                .transferId(TRANSFER_ID)
                .blocked(false)
                .suspicious(true)
                .suspiciousReason("Подозрительно")
                .build()));

        final String json = auditService.currentStateJson(EntityType.CARD_TRANSFER, CARD_ID);

        assertThat(json).contains("\"transferId\":100");
    }

    @Test
    @DisplayName("currentStateJson: бросает EntityNotFoundException")
    void shouldThrowWhenTransferMissing() {
        when(cardTransferRepository.findById(CARD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> auditService.currentStateJson(EntityType.CARD_TRANSFER, CARD_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
