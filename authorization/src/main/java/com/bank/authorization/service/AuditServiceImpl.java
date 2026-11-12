package com.bank.authorization.service;

import com.bank.authorization.entity.Audit;
import com.bank.authorization.entity.EntityType;
import com.bank.authorization.entity.OperationType;
import com.bank.authorization.exception.AuditSerializationException;
import com.bank.authorization.exception.EntityNotFoundException;
import com.bank.authorization.mapper.UserMapper;
import com.bank.authorization.repository.AuditRepository;
import com.bank.authorization.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private static final String SYSTEM_AUDITOR = "system";
    private static final String SERIALIZATION_ERROR = "Cannot serialize user state to JSON";
    private static final String USER_NOT_FOUND = "User with id %d not found";

    private final AuditRepository auditRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void logCreate(Object payload) {
        final OffsetDateTime now = OffsetDateTime.now();
        final Audit audit = Audit.builder()
                .entityType(EntityType.USER)
                .operationType(OperationType.CREATE)
                .createdBy(currentAuditor())
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
                .entityType(EntityType.USER)
                .operationType(OperationType.UPDATE)
                .createdBy(currentAuditor())
                .modifiedBy(currentAuditor())
                .createdAt(now)
                .modifiedAt(now)
                .entityJson(beforeJson)
                .newEntityJson(serialize(payload))
                .build();
        auditRepository.save(audit);
    }

    @Override
    @Transactional(readOnly = true)
    public String currentStateJson(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .map(this::serialize)
                .orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND, userId)));
    }

    private String currentAuditor() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return SYSTEM_AUDITOR;
        }
        return authentication.getName();
    }

    private String serialize(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new AuditSerializationException(SERIALIZATION_ERROR, exception);
        }
    }
}
