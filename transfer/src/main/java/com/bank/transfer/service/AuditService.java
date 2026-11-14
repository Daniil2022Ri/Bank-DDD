package com.bank.transfer.service;

import com.bank.transfer.entity.EntityType;

public interface AuditService {

    void logCreate(EntityType entityType, Object payload);

    void logUpdate(EntityType entityType, String beforeJson, Object payload);

    String currentStateJson(EntityType entityType, Long transferId);
}
