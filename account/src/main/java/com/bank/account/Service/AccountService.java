package com.bank.account.Service;

import com.bank.account.Dto.AccountDto;

public interface AccountService {

    AccountDto findBalanceAccountUser(Long userId);


}
