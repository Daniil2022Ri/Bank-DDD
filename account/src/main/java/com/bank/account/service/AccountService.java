package com.bank.account.service;

import com.bank.account.dto.AccountDto;
import java.util.List;

public interface AccountService {

    AccountDto create(AccountDto accountDto);

    AccountDto getById(Long accountId);

    List<AccountDto> getAll();

    AccountDto update(Long accountId, AccountDto accountDto);

    void delete(Long accountId);
}
