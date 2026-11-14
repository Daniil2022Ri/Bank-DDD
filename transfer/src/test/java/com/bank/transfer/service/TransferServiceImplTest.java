package com.bank.transfer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.transfer.dto.AccountTransferDto;
import com.bank.transfer.dto.CardTransferDto;
import com.bank.transfer.dto.PhoneTransferDto;
import com.bank.transfer.entity.AccountTransfer;
import com.bank.transfer.entity.CardTransfer;
import com.bank.transfer.entity.PhoneTransfer;
import com.bank.transfer.exception.DuplicateTransferException;
import com.bank.transfer.exception.EntityNotFoundException;
import com.bank.transfer.mapper.TransferMapperImpl;
import com.bank.transfer.repository.AccountTransferRepository;
import com.bank.transfer.repository.CardTransferRepository;
import com.bank.transfer.repository.PhoneTransferRepository;
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
class TransferServiceImplTest {

    private static final Long TRANSFER_ID = 1L;
    private static final Long ACCOUNT_NUMBER = 4081781000000000001L;
    private static final Long CARD_NUMBER = 4276000000000001L;
    private static final Long PHONE_NUMBER = 79990000000L;
    private static final Long ACCOUNT_DETAILS_ID = 55L;

    @Mock
    private AccountTransferRepository accountTransferRepository;

    @Mock
    private CardTransferRepository cardTransferRepository;

    @Mock
    private PhoneTransferRepository phoneTransferRepository;

    private TransferServiceImpl transferService;

    @BeforeEach
    void setUp() {
        transferService = new TransferServiceImpl(accountTransferRepository, cardTransferRepository,
                phoneTransferRepository, new TransferMapperImpl());
    }

    private AccountTransferDto accountDto() {
        return AccountTransferDto.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .amount(new BigDecimal("150.00"))
                .purpose("Перевод другу")
                .accountDetailsId(ACCOUNT_DETAILS_ID)
                .build();
    }

    private CardTransferDto cardDto() {
        return CardTransferDto.builder()
                .cardNumber(CARD_NUMBER)
                .amount(new BigDecimal("75.50"))
                .accountDetailsId(ACCOUNT_DETAILS_ID)
                .build();
    }

    private PhoneTransferDto phoneDto() {
        return PhoneTransferDto.builder()
                .phoneNumber(PHONE_NUMBER)
                .amount(new BigDecimal("10.00"))
                .accountDetailsId(ACCOUNT_DETAILS_ID)
                .build();
    }

    private AccountTransfer accountEntity() {
        final AccountTransfer transfer = new AccountTransfer();
        transfer.setId(TRANSFER_ID);
        transfer.setAccountNumber(ACCOUNT_NUMBER);
        transfer.setAmount(new BigDecimal("150.00"));
        transfer.setAccountDetailsId(ACCOUNT_DETAILS_ID);
        return transfer;
    }

    @Test
    @DisplayName("createAccount: сохраняет перевод по счёту")
    void createAccountShouldSaveTransfer() {
        when(accountTransferRepository.existsByAccountNumber(ACCOUNT_NUMBER)).thenReturn(false);
        when(accountTransferRepository.save(any(AccountTransfer.class))).thenAnswer(invocation -> {
            final AccountTransfer transfer = invocation.getArgument(0);
            transfer.setId(TRANSFER_ID);
            return transfer;
        });

        final AccountTransferDto result = transferService.createAccount(accountDto());

        assertThat(result.getId()).isEqualTo(TRANSFER_ID);
        assertThat(result.getAmount()).isEqualByComparingTo("150.00");
    }

    @Test
    @DisplayName("createAccount: запрещает дубликат номера счёта")
    void createAccountShouldRejectDuplicate() {
        when(accountTransferRepository.existsByAccountNumber(ACCOUNT_NUMBER)).thenReturn(true);

        assertThatThrownBy(() -> transferService.createAccount(accountDto()))
                .isInstanceOf(DuplicateTransferException.class)
                .hasMessageContaining(String.valueOf(ACCOUNT_NUMBER));
        verify(accountTransferRepository, never()).save(any(AccountTransfer.class));
    }

    @Test
    @DisplayName("createCard: запрещает дубликат номера карты")
    void createCardShouldRejectDuplicate() {
        when(cardTransferRepository.existsByCardNumber(CARD_NUMBER)).thenReturn(true);

        assertThatThrownBy(() -> transferService.createCard(cardDto()))
                .isInstanceOf(DuplicateTransferException.class);
    }

    @Test
    @DisplayName("createPhone: сохраняет перевод по телефону")
    void createPhoneShouldSaveTransfer() {
        when(phoneTransferRepository.save(any(PhoneTransfer.class))).thenAnswer(invocation -> {
            final PhoneTransfer transfer = invocation.getArgument(0);
            transfer.setId(TRANSFER_ID);
            return transfer;
        });

        final PhoneTransferDto result = transferService.createPhone(phoneDto());

        assertThat(result.getId()).isEqualTo(TRANSFER_ID);
        assertThat(result.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
    }

    @Test
    @DisplayName("getAccountById: возвращает перевод")
    void getAccountByIdShouldReturnTransfer() {
        when(accountTransferRepository.findById(TRANSFER_ID)).thenReturn(Optional.of(accountEntity()));

        final AccountTransferDto result = transferService.getAccountById(TRANSFER_ID);

        assertThat(result.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);
    }

    @Test
    @DisplayName("getAccountById: бросает EntityNotFoundException")
    void getAccountByIdShouldThrowWhenMissing() {
        when(accountTransferRepository.findById(TRANSFER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferService.getAccountById(TRANSFER_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.valueOf(TRANSFER_ID));
    }

    @Test
    @DisplayName("getAllCards: возвращает список переводов")
    void getAllCardsShouldReturnTransfers() {
        final CardTransfer transfer = new CardTransfer();
        transfer.setId(TRANSFER_ID);
        transfer.setCardNumber(CARD_NUMBER);
        when(cardTransferRepository.findAll()).thenReturn(List.of(transfer));

        assertThat(transferService.getAllCards()).hasSize(1);
    }

    @Test
    @DisplayName("updateAccount: обновляет сумму и цель")
    void updateAccountShouldModifyTransfer() {
        final AccountTransfer existing = accountEntity();
        when(accountTransferRepository.findById(TRANSFER_ID)).thenReturn(Optional.of(existing));
        when(accountTransferRepository.save(existing)).thenReturn(existing);

        final AccountTransferDto request = accountDto();
        request.setAmount(new BigDecimal("200.00"));
        request.setPurpose("Возврат долга");
        final AccountTransferDto result = transferService.updateAccount(TRANSFER_ID, request);

        assertThat(result.getAmount()).isEqualByComparingTo("200.00");
        assertThat(result.getPurpose()).isEqualTo("Возврат долга");
    }

    @Test
    @DisplayName("deletePhone: удаляет перевод")
    void deletePhoneShouldRemoveTransfer() {
        final PhoneTransfer existing = new PhoneTransfer();
        existing.setId(TRANSFER_ID);
        when(phoneTransferRepository.findById(TRANSFER_ID)).thenReturn(Optional.of(existing));

        transferService.deletePhone(TRANSFER_ID);

        verify(phoneTransferRepository).delete(existing);
    }

    @Test
    @DisplayName("deleteAccount: бросает EntityNotFoundException")
    void deleteAccountShouldThrowWhenMissing() {
        when(accountTransferRepository.findById(TRANSFER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferService.deleteAccount(TRANSFER_ID))
                .isInstanceOf(EntityNotFoundException.class);
        verify(accountTransferRepository, never()).delete(any(AccountTransfer.class));
    }
}
