package com.bank.publicinfo.service;

import com.bank.publicinfo.entity.EntityType;

public interface AuditService {

    void logCreate(EntityType entityType, Object payload);

    void logUpdate(EntityType entityType, String beforeJson, Object payload);

    String currentStateJson(EntityType entityType, Long entityId);
}
