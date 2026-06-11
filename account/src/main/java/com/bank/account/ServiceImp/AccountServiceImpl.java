package com.bank.account.ServiceImp;

import com.bank.account.Dto.AccountDto;
import com.bank.account.Entity.AccountEntity;
import com.bank.account.Mapper.MapperAccount;
import com.bank.account.Repository.AccountRepository;
import com.bank.account.Service.AccountService;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final MapperAccount mapperAccount;


    @Override
    public AccountDto findBalanceAccountUser(Long userId) {
        AccountEntity accountEntity = accountRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Аккаунт с ID " + userId + " Не найден" ));
        return mapperAccount.accountEntityToDto(accountEntity);
    }
}
