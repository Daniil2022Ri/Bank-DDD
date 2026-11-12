package com.bank.antifraud.service;

import com.bank.antifraud.entity.Audit;
import com.bank.antifraud.entity.EntityType;
import com.bank.antifraud.entity.OperationType;
import com.bank.antifraud.exception.AuditSerializationException;
import com.bank.antifraud.exception.EntityNotFoundException;
import com.bank.antifraud.mapper.SuspiciousTransferMapper;
import com.bank.antifraud.repository.AuditRepository;
import com.bank.antifraud.repository.SuspiciousAccountTransferRepository;
import com.bank.antifraud.repository.SuspiciousCardTransferRepository;
import com.bank.antifraud.repository.SuspiciousPhoneTransferRepository;
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
    private static final String SERIALIZATION_ERROR = "Cannot serialize transfer state to JSON";
    private static final String CARD_NOT_FOUND = "Suspicious card transfer with id %d not found";
    private static final String PHONE_NOT_FOUND = "Suspicious phone transfer with id %d not found";
    private static final String ACCOUNT_NOT_FOUND = "Suspicious account transfer with id %d not found";
    private static final String UNSUPPORTED_ENTITY = "Unsupported entity type: ";

    private final AuditRepository auditRepository;
    private final SuspiciousCardTransferRepository cardTransferRepository;
    private final SuspiciousPhoneTransferRepository phoneTransferRepository;
    private final SuspiciousAccountTransferRepository accountTransferRepository;
    private final SuspiciousTransferMapper mapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void logCreate(EntityType entityType, Object payload) {
        final OffsetDateTime now = OffsetDateTime.now();
        final Audit audit = Audit.builder()
                .entityType(entityType)
                .operationType(OperationType.CREATE)
                .createdBy(SYSTEM_AUDITOR)
                .createdAt(now)
                .entityJson(serialize(payload))
                .build();
        auditRepository.save(audit);
    }

    @Override
    @Transactional
    public void logUpdate(EntityType entityType, String beforeJson, Object payload) {
        final OffsetDateTime now = OffsetDateTime.now();
        final Audit audit = Audit.builder()
                .entityType(entityType)
                .operationType(OperationType.UPDATE)
                .createdBy(SYSTEM_AUDITOR)
                .modifiedBy(SYSTEM_AUDITOR)
                .createdAt(now)
                .modifiedAt(now)
                .entityJson(beforeJson)
                .newEntityJson(serialize(payload))
                .build();
        auditRepository.save(audit);
    }

    @Override
    @Transactional(readOnly = true)
    public String currentStateJson(EntityType entityType, Long transferId) {
        return switch (entityType) {
            case CARD_TRANSFER -> cardTransferRepository.findById(transferId)
                    .map(mapper::toCardDto)
                    .map(this::serialize)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(CARD_NOT_FOUND, transferId)));
            case PHONE_TRANSFER -> phoneTransferRepository.findById(transferId)
                    .map(mapper::toPhoneDto)
                    .map(this::serialize)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(PHONE_NOT_FOUND, transferId)));
            case ACCOUNT_TRANSFER -> accountTransferRepository.findById(transferId)
                    .map(mapper::toAccountDto)
                    .map(this::serialize)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(ACCOUNT_NOT_FOUND, transferId)));
            default -> throw new IllegalArgumentException(UNSUPPORTED_ENTITY + entityType);
        };
    }

    private String serialize(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new AuditSerializationException(SERIALIZATION_ERROR, exception);
        }
    }
}
