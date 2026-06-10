package com.bank.account.ServiceImp;

import com.bank.account.Dto.AccountDto;
import com.bank.account.Entity.AccountEntity;
import com.bank.account.Mapper.MapperAccount;
import com.bank.account.Repository.AccountRepository;
import com.bank.account.Service.AccountService;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final MapperAccount mapperAccount;

    public AccountServiceImpl(AccountRepository accountRepository, MapperAccount mapperAccount) {
        this.accountRepository = accountRepository;
        this.mapperAccount = mapperAccount;
    }

    @Override
    public double findBalanceAccountUser(Long userId) {

        AccountEntity accountEntity = accountRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Аккаунт с таким ID не найден"));
        AccountDto accountDto = mapperAccount.accountEntityToDto(accountEntity);
        return accountDto.getBalanceAccount();

    }
}
