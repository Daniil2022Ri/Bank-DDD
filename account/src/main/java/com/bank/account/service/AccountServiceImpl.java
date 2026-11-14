package com.bank.account.service;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Account;
import com.bank.account.exception.AccountAlreadyExistsException;
import com.bank.account.exception.EntityNotFoundException;
import com.bank.account.mapper.AccountMapper;
import com.bank.account.repository.AccountRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private static final String ACCOUNT_NOT_FOUND = "Account with id %d not found";
    private static final String ACCOUNT_NUMBER_EXISTS = "Account with number %d already exists";
    private static final String BANK_DETAILS_EXISTS = "Account with bank details id %d already exists";

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountDto create(AccountDto accountDto) {
        ensureUnique(accountDto);
        final Account entity = accountMapper.toEntity(accountDto);
        recalculateNegativeBalance(entity);
        final Account savedAccount = accountRepository.save(entity);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getById(Long accountId) {
        return accountMapper.toDto(findAccount(accountId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getAll() {
        return accountMapper.toDtoList(accountRepository.findAll());
    }

    @Override
    public AccountDto update(Long accountId, AccountDto accountDto) {
        final Account existingAccount = findAccount(accountId);
        ensureUniqueForUpdate(accountDto, accountId);
        accountMapper.updateEntity(accountDto, existingAccount);
        recalculateNegativeBalance(existingAccount);
        final Account savedAccount = accountRepository.save(existingAccount);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    public void delete(Long accountId) {
        accountRepository.delete(findAccount(accountId));
    }

    private Account findAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ACCOUNT_NOT_FOUND, accountId)));
    }

    private void ensureUnique(AccountDto accountDto) {
        if (accountRepository.existsByAccountNumber(accountDto.getAccountNumber())) {
            throw new AccountAlreadyExistsException(String.format(ACCOUNT_NUMBER_EXISTS,
                    accountDto.getAccountNumber()));
        }
        if (accountRepository.existsByBankDetailsId(accountDto.getBankDetailsId())) {
            throw new AccountAlreadyExistsException(String.format(BANK_DETAILS_EXISTS,
                    accountDto.getBankDetailsId()));
        }
    }

    private void ensureUniqueForUpdate(AccountDto accountDto, Long accountId) {
        if (accountRepository.existsByAccountNumberAndIdNot(accountDto.getAccountNumber(), accountId)) {
            throw new AccountAlreadyExistsException(String.format(ACCOUNT_NUMBER_EXISTS,
                    accountDto.getAccountNumber()));
        }
        if (accountRepository.existsByBankDetailsIdAndIdNot(accountDto.getBankDetailsId(), accountId)) {
            throw new AccountAlreadyExistsException(String.format(BANK_DETAILS_EXISTS,
                    accountDto.getBankDetailsId()));
        }
    }

    private void recalculateNegativeBalance(Account account) {
        account.setNegativeBalance(account.getMoney() != null && account.getMoney().signum() < 0);
    }
}
