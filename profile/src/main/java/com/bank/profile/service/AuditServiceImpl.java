package com.bank.profile.service;

import com.bank.profile.entity.Audit;
import com.bank.profile.entity.EntityType;
import com.bank.profile.entity.OperationType;
import com.bank.profile.exception.AuditSerializationException;
import com.bank.profile.exception.EntityNotFoundException;
import com.bank.profile.mapper.ProfileMapper;
import com.bank.profile.repository.AuditRepository;
import com.bank.profile.repository.ProfileRepository;
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
    private static final String SERIALIZATION_ERROR = "Cannot serialize profile state to JSON";
    private static final String PROFILE_NOT_FOUND = "Profile with id %d not found";

    private final AuditRepository auditRepository;
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void logCreate(Object payload) {
        final OffsetDateTime now = OffsetDateTime.now();
        final Audit audit = Audit.builder()
                .entityType(EntityType.PROFILE)
                .operationType(OperationType.CREATE)
                .createdBy(SYSTEM_AUDITOR)
                .createdAt(now)
                .entityJson(serialize(payload))
                .build();
        auditRepository.save(audit);
    }

    @Override
    @Transactional
    public void logUpdate(String beforeJson, Object payload) {
        final OffsetDateTime now = OffsetDateTime.now();
        final Audit audit = Audit.builder()
                .entityType(EntityType.PROFILE)
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
    public String currentStateJson(Long profileId) {
        return profileRepository.findById(profileId)
                .map(profileMapper::toDto)
                .map(this::serialize)
                .orElseThrow(() -> new EntityNotFoundException(String.format(PROFILE_NOT_FOUND, profileId)));
    }

    private String serialize(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new AuditSerializationException(SERIALIZATION_ERROR, exception);
        }
    }
}
