package com.bank.profile.service;

public interface AuditService {

    void logCreate(Object payload);

    void logUpdate(String beforeJson, Object payload);

    String currentStateJson(Long profileId);
}
