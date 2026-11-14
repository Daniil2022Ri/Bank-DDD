package com.bank.account.service;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Audit;
import com.bank.account.entity.EntityType;
import com.bank.account.entity.OperationType;
import com.bank.account.exception.AuditSerializationException;
import com.bank.account.exception.EntityNotFoundException;
import com.bank.account.mapper.AccountMapper;
import com.bank.account.repository.AccountRepository;
import com.bank.account.repository.AuditRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private static final String SYSTEM_AUDITOR = "system";
    private static final String ACCOUNT_NOT_FOUND = "Account with id %d not found";
    private static final String SERIALIZATION_ERROR = "Cannot serialize account state to JSON";

    private final AuditRepository auditRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void logCreate(AccountDto createdAccount) {
        final OffsetDateTime now = OffsetDateTime.now();
        final Audit audit = Audit.builder()
                .entityType(EntityType.ACCOUNT)
                .operationType(OperationType.CREATE)
                .createdBy(SYSTEM_AUDITOR)
                .createdAt(now)
                .entityJson(serialize(createdAccount))
                .build();
        auditRepository.save(audit);
    }

    @Override
    @Transactional
    public void logUpdate(String beforeJson, AccountDto updatedAccount) {
        final OffsetDateTime now = OffsetDateTime.now();
        final Audit audit = Audit.builder()
                .entityType(EntityType.ACCOUNT)
                .operationType(OperationType.UPDATE)
                .createdBy(SYSTEM_AUDITOR)
                .modifiedBy(SYSTEM_AUDITOR)
                .createdAt(now)
                .modifiedAt(now)
                .entityJson(beforeJson)
                .newEntityJson(serialize(updatedAccount))
                .build();
        auditRepository.save(audit);
    }

    @Override
    @Transactional(readOnly = true)
    public String currentStateJson(Long accountId) {
        return accountRepository.findById(accountId)
                .map(accountMapper::toDto)
                .map(this::serialize)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ACCOUNT_NOT_FOUND, accountId)));
    }

    private String serialize(AccountDto accountDto) {
        try {
            return objectMapper.writeValueAsString(accountDto);
        } catch (JsonProcessingException exception) {
            throw new AuditSerializationException(SERIALIZATION_ERROR, exception);
        }
    }
}
