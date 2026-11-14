package com.bank.antifraud.service;

import com.bank.antifraud.entity.EntityType;

public interface AuditService {

    void logCreate(EntityType entityType, Object payload);

    void logUpdate(EntityType entityType, String beforeJson, Object payload);

    String currentStateJson(EntityType entityType, Long transferId);
}
