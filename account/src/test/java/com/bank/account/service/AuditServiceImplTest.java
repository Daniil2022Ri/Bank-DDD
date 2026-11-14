package com.bank.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Account;
import com.bank.account.entity.Audit;
import com.bank.account.entity.EntityType;
import com.bank.account.entity.OperationType;
import com.bank.account.exception.EntityNotFoundException;
import com.bank.account.mapper.AccountMapperImpl;
import com.bank.account.repository.AccountRepository;
import com.bank.account.repository.AuditRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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

    @Mock
    private AuditRepository auditRepository;

    @Mock
    private AccountRepository accountRepository;

    private AuditServiceImpl auditService;

    @BeforeEach
    void setUp() {
        auditService = new AuditServiceImpl(auditRepository, accountRepository,
                new AccountMapperImpl(), new ObjectMapper());
    }

    private AccountDto accountDto() {
        return AccountDto.builder()
                .id(1L)
                .passportId(11L)
                .accountNumber(4081781000000000001L)
                .bankDetailsId(22L)
                .money(new BigDecimal("10.00"))
                .negativeBalance(false)
                .profileId(33L)
                .build();
    }

    @Test
    @DisplayName("logCreate: пишет аудит операции CREATE")
    void shouldLogCreate() {
        final ArgumentCaptor<Audit> captor = ArgumentCaptor.forClass(Audit.class);

        auditService.logCreate(accountDto());

        verify(auditRepository).save(captor.capture());
        final Audit audit = captor.getValue();
        assertThat(audit.getOperationType()).isEqualTo(OperationType.CREATE);
        assertThat(audit.getEntityType()).isEqualTo(EntityType.ACCOUNT);
        assertThat(audit.getCreatedBy()).isEqualTo("system");
        assertThat(audit.getCreatedAt()).isNotNull();
        assertThat(audit.getEntityJson()).contains("4081781000000000001");
    }

    @Test
    @DisplayName("logUpdate: пишет старое и новое состояние")
    void shouldLogUpdate() {
        final ArgumentCaptor<Audit> captor = ArgumentCaptor.forClass(Audit.class);

        auditService.logUpdate("{\"money\":10.00}", accountDto());

        verify(auditRepository).save(captor.capture());
        final Audit audit = captor.getValue();
        assertThat(audit.getOperationType()).isEqualTo(OperationType.UPDATE);
        assertThat(audit.getEntityJson()).isEqualTo("{\"money\":10.00}");
        assertThat(audit.getNewEntityJson()).contains("4081781000000000001");
        assertThat(audit.getModifiedAt()).isNotNull();
    }

    @Test
    @DisplayName("currentStateJson: сериализует текущее состояние счёта")
    void shouldReturnCurrentStateJson() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(Account.builder()
                .id(1L)
                .accountNumber(4081781000000000001L)
                .money(new BigDecimal("10.00"))
                .build()));

        final String json = auditService.currentStateJson(1L);

        assertThat(json).contains("4081781000000000001");
    }

    @Test
    @DisplayName("currentStateJson: бросает EntityNotFoundException")
    void shouldThrowWhenAccountMissing() {
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> auditService.currentStateJson(2L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
