package com.bank.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Account;
import com.bank.account.exception.AccountAlreadyExistsException;
import com.bank.account.exception.EntityNotFoundException;
import com.bank.account.mapper.AccountMapper;
import com.bank.account.mapper.AccountMapperImpl;
import com.bank.account.repository.AccountRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    private static final Long ACCOUNT_ID = 1L;
    private static final Long PASSPORT_ID = 11L;
    private static final Long ACCOUNT_NUMBER = 4081781000000000001L;
    private static final Long BANK_DETAILS_ID = 22L;
    private static final Long PROFILE_ID = 33L;

    @Mock
    private AccountRepository accountRepository;

    private AccountMapper accountMapper;
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        accountMapper = new AccountMapperImpl();
        accountService = new AccountServiceImpl(accountRepository, accountMapper);
    }

    private AccountDto accountDto(BigDecimal money) {
        return AccountDto.builder()
                .passportId(PASSPORT_ID)
                .accountNumber(ACCOUNT_NUMBER)
                .bankDetailsId(BANK_DETAILS_ID)
                .money(money)
                .profileId(PROFILE_ID)
                .build();
    }

    private Account account(BigDecimal money) {
        return Account.builder()
                .id(ACCOUNT_ID)
                .passportId(PASSPORT_ID)
                .accountNumber(ACCOUNT_NUMBER)
                .bankDetailsId(BANK_DETAILS_ID)
                .money(money)
                .negativeBalance(false)
                .profileId(PROFILE_ID)
                .build();
    }

    @Test
    @DisplayName("create: сохраняет счёт и вычисляет negativeBalance = false для положительного баланса")
    void createShouldSaveAccountWithPositiveBalance() {
        final AccountDto request = accountDto(new BigDecimal("100.50"));
        when(accountRepository.existsByAccountNumber(ACCOUNT_NUMBER)).thenReturn(false);
        when(accountRepository.existsByBankDetailsId(BANK_DETAILS_ID)).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            final Account entity = invocation.getArgument(0);
            entity.setId(ACCOUNT_ID);
            return entity;
        });

        final AccountDto result = accountService.create(request);

        assertThat(result.getId()).isEqualTo(ACCOUNT_ID);
        assertThat(result.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);
        assertThat(result.getMoney()).isEqualByComparingTo("100.50");
        assertThat(result.getNegativeBalance()).isFalse();
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("create: выставляет negativeBalance = true для отрицательного баланса")
    void createShouldMarkNegativeBalance() {
        final AccountDto request = accountDto(new BigDecimal("-15.00"));
        when(accountRepository.existsByAccountNumber(ACCOUNT_NUMBER)).thenReturn(false);
        when(accountRepository.existsByBankDetailsId(BANK_DETAILS_ID)).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final AccountDto result = accountService.create(request);

        assertThat(result.getNegativeBalance()).isTrue();
    }

    @Test
    @DisplayName("create: запрещает дубликат номера счёта")
    void createShouldRejectDuplicateAccountNumber() {
        when(accountRepository.existsByAccountNumber(ACCOUNT_NUMBER)).thenReturn(true);

        assertThatThrownBy(() -> accountService.create(accountDto(BigDecimal.TEN)))
                .isInstanceOf(AccountAlreadyExistsException.class)
                .hasMessageContaining(String.valueOf(ACCOUNT_NUMBER));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("create: запрещает дубликат реквизитов банка")
    void createShouldRejectDuplicateBankDetails() {
        when(accountRepository.existsByAccountNumber(ACCOUNT_NUMBER)).thenReturn(false);
        when(accountRepository.existsByBankDetailsId(BANK_DETAILS_ID)).thenReturn(true);

        assertThatThrownBy(() -> accountService.create(accountDto(BigDecimal.TEN)))
                .isInstanceOf(AccountAlreadyExistsException.class)
                .hasMessageContaining(String.valueOf(BANK_DETAILS_ID));
    }

    @Test
    @DisplayName("getById: возвращает счёт")
    void getByIdShouldReturnAccount() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account(BigDecimal.TEN)));

        final AccountDto result = accountService.getById(ACCOUNT_ID);

        assertThat(result.getId()).isEqualTo(ACCOUNT_ID);
        assertThat(result.getProfileId()).isEqualTo(PROFILE_ID);
    }

    @Test
    @DisplayName("getById: бросает EntityNotFoundException")
    void getByIdShouldThrowWhenMissing() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getById(ACCOUNT_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.valueOf(ACCOUNT_ID));
    }

    @Test
    @DisplayName("getAll: возвращает все счета")
    void getAllShouldReturnAllAccounts() {
        when(accountRepository.findAll()).thenReturn(List.of(account(BigDecimal.ONE),
                account(new BigDecimal("2.00"))));

        final List<AccountDto> result = accountService.getAll();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("update: обновляет поля и пересчитывает negativeBalance")
    void updateShouldModifyAccount() {
        final Account existing = account(new BigDecimal("10.00"));
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(existing));
        when(accountRepository.existsByAccountNumberAndIdNot(ACCOUNT_NUMBER, ACCOUNT_ID)).thenReturn(false);
        when(accountRepository.existsByBankDetailsIdAndIdNot(BANK_DETAILS_ID, ACCOUNT_ID)).thenReturn(false);
        when(accountRepository.save(existing)).thenReturn(existing);

        final AccountDto result = accountService.update(ACCOUNT_ID, accountDto(new BigDecimal("-3.25")));

        assertThat(result.getMoney()).isEqualByComparingTo("-3.25");
        assertThat(result.getNegativeBalance()).isTrue();
    }

    @Test
    @DisplayName("update: бросает EntityNotFoundException для несуществующего счёта")
    void updateShouldThrowWhenMissing() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.update(ACCOUNT_ID, accountDto(BigDecimal.TEN)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("delete: удаляет существующий счёт")
    void deleteShouldRemoveAccount() {
        final Account existing = account(BigDecimal.TEN);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(existing));

        accountService.delete(ACCOUNT_ID);

        verify(accountRepository).delete(existing);
    }

    @Test
    @DisplayName("delete: бросает EntityNotFoundException для несуществующего счёта")
    void deleteShouldThrowWhenMissing() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.delete(ACCOUNT_ID))
                .isInstanceOf(EntityNotFoundException.class);
        verify(accountRepository, never()).delete(any(Account.class));
    }
}
