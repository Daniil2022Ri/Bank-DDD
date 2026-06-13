package com.bank.account.Service;

import com.bank.account.Dto.AccountDto;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    AccountDto openAccount(AccountDto accountDto);
    AccountDto getAccountById(Long id);
    List<AccountDto> getAccountsByProfile(Long profileId);
    AccountDto blockAccount(Long id);
    AccountDto closeAccount(Long id);
    AccountDto updateBalance(Long id, BigDecimal amount);

}
