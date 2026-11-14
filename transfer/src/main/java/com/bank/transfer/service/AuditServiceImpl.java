package com.bank.transfer.service;

import com.bank.transfer.entity.Audit;
import com.bank.transfer.entity.EntityType;
import com.bank.transfer.entity.OperationType;
import com.bank.transfer.exception.AuditSerializationException;
import com.bank.transfer.exception.EntityNotFoundException;
import com.bank.transfer.mapper.TransferMapper;
import com.bank.transfer.repository.AccountTransferRepository;
import com.bank.transfer.repository.AuditRepository;
import com.bank.transfer.repository.CardTransferRepository;
import com.bank.transfer.repository.PhoneTransferRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private static final String SYSTEM_AUDITOR = "system";
    private static final String SERIALIZATION_ERROR = "Cannot serialize transfer state to JSON";
    private static final String ACCOUNT_NOT_FOUND = "Account transfer with id %d not found";
    private static final String CARD_NOT_FOUND = "Card transfer with id %d not found";
    private static final String PHONE_NOT_FOUND = "Phone transfer with id %d not found";
    private static final String UNSUPPORTED_ENTITY = "Unsupported entity type: ";

    private final AuditRepository auditRepository;
    private final AccountTransferRepository accountTransferRepository;
    private final CardTransferRepository cardTransferRepository;
    private final PhoneTransferRepository phoneTransferRepository;
    private final TransferMapper transferMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void logCreate(EntityType entityType, Object payload) {
        final LocalDateTime now = LocalDateTime.now();
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
        final LocalDateTime now = LocalDateTime.now();
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
            case ACCOUNT_TRANSFER -> accountTransferRepository.findById(transferId)
                    .map(transferMapper::toAccountDto)
                    .map(this::serialize)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format(ACCOUNT_NOT_FOUND, transferId)));
            case CARD_TRANSFER -> cardTransferRepository.findById(transferId)
                    .map(transferMapper::toCardDto)
                    .map(this::serialize)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format(CARD_NOT_FOUND, transferId)));
            case PHONE_TRANSFER -> phoneTransferRepository.findById(transferId)
                    .map(transferMapper::toPhoneDto)
                    .map(this::serialize)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format(PHONE_NOT_FOUND, transferId)));
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
