package com.bank.publicinfo.service;

import com.bank.publicinfo.entity.Audit;
import com.bank.publicinfo.entity.EntityType;
import com.bank.publicinfo.entity.OperationType;
import com.bank.publicinfo.exception.AuditSerializationException;
import com.bank.publicinfo.exception.EntityNotFoundException;
import com.bank.publicinfo.mapper.PublicInfoMapper;
import com.bank.publicinfo.repository.AtmRepository;
import com.bank.publicinfo.repository.AuditRepository;
import com.bank.publicinfo.repository.BankDetailsRepository;
import com.bank.publicinfo.repository.BranchRepository;
import com.bank.publicinfo.repository.CertificateRepository;
import com.bank.publicinfo.repository.LicenseRepository;
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
    private static final String SERIALIZATION_ERROR = "Cannot serialize public info state to JSON";
    private static final String BANK_DETAILS_NOT_FOUND = "Bank details with id %d not found";
    private static final String BRANCH_NOT_FOUND = "Branch with id %d not found";
    private static final String ATM_NOT_FOUND = "Atm with id %d not found";
    private static final String CERTIFICATE_NOT_FOUND = "Certificate with id %d not found";
    private static final String LICENSE_NOT_FOUND = "License with id %d not found";
    private static final String UNSUPPORTED_ENTITY = "Unsupported entity type: ";

    private final AuditRepository auditRepository;
    private final BankDetailsRepository bankDetailsRepository;
    private final BranchRepository branchRepository;
    private final AtmRepository atmRepository;
    private final CertificateRepository certificateRepository;
    private final LicenseRepository licenseRepository;
    private final PublicInfoMapper publicInfoMapper;
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
    public String currentStateJson(EntityType entityType, Long entityId) {
        return switch (entityType) {
            case BANK_DETAILS -> bankDetailsRepository.findById(entityId)
                    .map(publicInfoMapper::toBankDetailsDto)
                    .map(this::serialize)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format(BANK_DETAILS_NOT_FOUND, entityId)));
            case BRANCH -> branchRepository.findById(entityId)
                    .map(publicInfoMapper::toBranchDto)
                    .map(this::serialize)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format(BRANCH_NOT_FOUND, entityId)));
            case ATM -> atmRepository.findById(entityId)
                    .map(publicInfoMapper::toAtmDto)
                    .map(this::serialize)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format(ATM_NOT_FOUND, entityId)));
            case CERTIFICATE -> certificateRepository.findById(entityId)
                    .map(publicInfoMapper::toCertificateDto)
                    .map(this::serialize)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format(CERTIFICATE_NOT_FOUND, entityId)));
            case LICENSE -> licenseRepository.findById(entityId)
                    .map(publicInfoMapper::toLicenseDto)
                    .map(this::serialize)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format(LICENSE_NOT_FOUND, entityId)));
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
