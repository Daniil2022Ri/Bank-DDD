package com.bank.account.service;

import com.bank.account.dto.AccountDto;

public interface AuditService {

    void logCreate(AccountDto createdAccount);

    void logUpdate(String beforeJson, AccountDto updatedAccount);

    String currentStateJson(Long accountId);
}
