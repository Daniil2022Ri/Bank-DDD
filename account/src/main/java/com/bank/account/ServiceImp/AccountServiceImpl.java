package com.bank.account.ServiceImp;

import com.bank.account.Dto.AccountDto;
import com.bank.account.Entity.AccountEntity;
import com.bank.account.Entity.Status;
import com.bank.account.Mapper.MapperAccount;
import com.bank.account.Repository.AccountRepository;
import com.bank.account.Service.AccountService;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final MapperAccount mapperAccount;


    @Override
    public AccountDto openAccount(AccountDto dto) {
        AccountEntity entity = mapperAccount.accountDtoToEntity(dto);

        return mapperAccount.accountEntityToDto(accountRepository.save(entity));
    }

    @Override
    public AccountDto getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(mapperAccount::accountEntityToDto)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + id));
    }

    @Override
    public List<AccountDto> getAccountsByProfile(Long profileId) {
        return accountRepository.findById(profileId).stream()
                .map(mapperAccount::accountEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDto blockAccount(Long id) {
        AccountEntity account = findByIdOrThrow(id);
        account.setStatusAccount(Status.BLOCKED);
        return mapperAccount.accountEntityToDto(accountRepository.save(account));
    }

    @Override
    public AccountDto closeAccount(Long id) {
        AccountEntity account = findByIdOrThrow(id);
        account.setStatusAccount(Status.CLOSED);
        return mapperAccount.accountEntityToDto(accountRepository.save(account));
    }

    @Override
    public AccountDto updateBalance(Long id, BigDecimal amount) {
        AccountEntity account = findByIdOrThrow(id);
        account.setBalanceAccount(account.getBalanceAccount().add(amount));
        return mapperAccount.accountEntityToDto(accountRepository.save(account));
    }

    private AccountEntity findByIdOrThrow(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + id));
    }
}

