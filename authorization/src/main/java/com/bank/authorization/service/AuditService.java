package com.bank.authorization.service;

public interface AuditService {

    void logCreate(Object payload);

    void logUpdate(String beforeJson, Object payload);

    String currentStateJson(Long userId);
}
