package com.bank.antifraud.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.antifraud.dto.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.SuspiciousPhoneTransferDto;
import com.bank.antifraud.entity.SuspiciousAccountTransfer;
import com.bank.antifraud.entity.SuspiciousCardTransfer;
import com.bank.antifraud.entity.SuspiciousPhoneTransfer;
import com.bank.antifraud.exception.DuplicateTransferException;
import com.bank.antifraud.exception.EntityNotFoundException;
import com.bank.antifraud.exception.TransferValidationException;
import com.bank.antifraud.mapper.SuspiciousTransferMapperImpl;
import com.bank.antifraud.repository.SuspiciousAccountTransferRepository;
import com.bank.antifraud.repository.SuspiciousCardTransferRepository;
import com.bank.antifraud.repository.SuspiciousPhoneTransferRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SuspiciousTransferServiceImplTest {

    private static final Long CARD_ID = 1L;
    private static final Long PHONE_ID = 2L;
    private static final Long ACCOUNT_ID = 3L;
    private static final Long TRANSFER_ID = 100L;
    private static final String SUSPICIOUS_REASON = "Превышен лимит операций";
    private static final String BLOCKED_REASON = "Подозрительная активность";

    @Mock
    private SuspiciousCardTransferRepository cardTransferRepository;

    @Mock
    private SuspiciousPhoneTransferRepository phoneTransferRepository;

    @Mock
    private SuspiciousAccountTransferRepository accountTransferRepository;

    private SuspiciousTransferServiceImpl transferService;

    @BeforeEach
    void setUp() {
        transferService = new SuspiciousTransferServiceImpl(cardTransferRepository, phoneTransferRepository,
                accountTransferRepository, new SuspiciousTransferMapperImpl());
    }

    private SuspiciousCardTransferDto cardDto() {
        return SuspiciousCardTransferDto.builder()
                .transferId(TRANSFER_ID)
                .blocked(false)
                .suspicious(true)
                .suspiciousReason(SUSPICIOUS_REASON)
                .build();
    }

    private SuspiciousPhoneTransferDto phoneDto() {
        return SuspiciousPhoneTransferDto.builder()
                .transferId(TRANSFER_ID)
                .blocked(false)
                .suspicious(true)
                .suspiciousReason(SUSPICIOUS_REASON)
                .build();
    }

    private SuspiciousAccountTransferDto accountDto() {
        return SuspiciousAccountTransferDto.builder()
                .transferId(TRANSFER_ID)
                .blocked(false)
                .suspicious(true)
                .suspiciousReason(SUSPICIOUS_REASON)
                .build();
    }

    private SuspiciousCardTransfer cardEntity() {
        return SuspiciousCardTransfer.builder()
                .id(CARD_ID)
                .transferId(TRANSFER_ID)
                .blocked(false)
                .suspicious(true)
                .suspiciousReason(SUSPICIOUS_REASON)
                .build();
    }

    @Test
    @DisplayName("createCard: сохраняет карточный перевод")
    void createCardShouldSaveTransfer() {
        when(cardTransferRepository.existsByTransferId(TRANSFER_ID)).thenReturn(false);
        when(cardTransferRepository.save(any(SuspiciousCardTransfer.class))).thenAnswer(invocation -> {
            final SuspiciousCardTransfer entity = invocation.getArgument(0);
            entity.setId(CARD_ID);
            return entity;
        });

        final SuspiciousCardTransferDto result = transferService.createCard(cardDto());

        assertThat(result.getId()).isEqualTo(CARD_ID);
        assertThat(result.getTransferId()).isEqualTo(TRANSFER_ID);
        assertThat(result.getSuspicious()).isTrue();
    }

    @Test
    @DisplayName("createCard: запрещает дубликат transferId")
    void createCardShouldRejectDuplicate() {
        when(cardTransferRepository.existsByTransferId(TRANSFER_ID)).thenReturn(true);

        assertThatThrownBy(() -> transferService.createCard(cardDto()))
                .isInstanceOf(DuplicateTransferException.class)
                .hasMessageContaining(String.valueOf(TRANSFER_ID));
        verify(cardTransferRepository, never()).save(any(SuspiciousCardTransfer.class));
    }

    @Test
    @DisplayName("createCard: требует причину блокировки для заблокированного перевода")
    void createCardShouldRequireBlockedReason() {
        final SuspiciousCardTransferDto request = cardDto();
        request.setBlocked(true);

        assertThatThrownBy(() -> transferService.createCard(request))
                .isInstanceOf(TransferValidationException.class)
                .hasMessageContaining("blockedReason");
    }

    @Test
    @DisplayName("createCard: требует причину подозрительности")
    void createCardShouldRequireSuspiciousReason() {
        final SuspiciousCardTransferDto request = cardDto();
        request.setSuspiciousReason(" ");

        assertThatThrownBy(() -> transferService.createCard(request))
                .isInstanceOf(TransferValidationException.class)
                .hasMessageContaining("suspiciousReason");
    }

    @Test
    @DisplayName("getCardById: возвращает перевод")
    void getCardByIdShouldReturnTransfer() {
        when(cardTransferRepository.findById(CARD_ID)).thenReturn(Optional.of(cardEntity()));

        final SuspiciousCardTransferDto result = transferService.getCardById(CARD_ID);

        assertThat(result.getId()).isEqualTo(CARD_ID);
    }

    @Test
    @DisplayName("getCardById: бросает EntityNotFoundException")
    void getCardByIdShouldThrowWhenMissing() {
        when(cardTransferRepository.findById(CARD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferService.getCardById(CARD_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.valueOf(CARD_ID));
    }

    @Test
    @DisplayName("getAllCards: возвращает список переводов")
    void getAllCardsShouldReturnList() {
        when(cardTransferRepository.findAll()).thenReturn(List.of(cardEntity()));

        final List<SuspiciousCardTransferDto> result = transferService.getAllCards();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("updateCard: обновляет флаги и причины, не меняя transferId")
    void updateCardShouldModifyTransfer() {
        final SuspiciousCardTransfer existing = cardEntity();
        when(cardTransferRepository.findById(CARD_ID)).thenReturn(Optional.of(existing));
        when(cardTransferRepository.save(existing)).thenReturn(existing);

        final SuspiciousCardTransferDto request = cardDto();
        request.setTransferId(999L);
        request.setBlocked(true);
        request.setBlockedReason(BLOCKED_REASON);

        final SuspiciousCardTransferDto result = transferService.updateCard(CARD_ID, request);

        assertThat(result.getTransferId()).isEqualTo(TRANSFER_ID);
        assertThat(result.getBlocked()).isTrue();
        assertThat(result.getBlockedReason()).isEqualTo(BLOCKED_REASON);
    }

    @Test
    @DisplayName("updateCard: бросает EntityNotFoundException")
    void updateCardShouldThrowWhenMissing() {
        when(cardTransferRepository.findById(CARD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferService.updateCard(CARD_ID, cardDto()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("deleteCard: удаляет перевод")
    void deleteCardShouldRemoveTransfer() {
        final SuspiciousCardTransfer existing = cardEntity();
        when(cardTransferRepository.findById(CARD_ID)).thenReturn(Optional.of(existing));

        transferService.deleteCard(CARD_ID);

        verify(cardTransferRepository).delete(existing);
    }

    @Test
    @DisplayName("deleteCard: бросает EntityNotFoundException")
    void deleteCardShouldThrowWhenMissing() {
        when(cardTransferRepository.findById(CARD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferService.deleteCard(CARD_ID))
                .isInstanceOf(EntityNotFoundException.class);
        verify(cardTransferRepository, never()).delete(any(SuspiciousCardTransfer.class));
    }

    @Test
    @DisplayName("createPhone: сохраняет телефонный перевод")
    void createPhoneShouldSaveTransfer() {
        when(phoneTransferRepository.existsByTransferId(TRANSFER_ID)).thenReturn(false);
        when(phoneTransferRepository.save(any(SuspiciousPhoneTransfer.class))).thenAnswer(invocation -> {
            final SuspiciousPhoneTransfer entity = invocation.getArgument(0);
            entity.setId(PHONE_ID);
            return entity;
        });

        final SuspiciousPhoneTransferDto result = transferService.createPhone(phoneDto());

        assertThat(result.getId()).isEqualTo(PHONE_ID);
    }

    @Test
    @DisplayName("getPhoneById: бросает EntityNotFoundException")
    void getPhoneByIdShouldThrowWhenMissing() {
        when(phoneTransferRepository.findById(PHONE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferService.getPhoneById(PHONE_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("updatePhone: обновляет перевод")
    void updatePhoneShouldModifyTransfer() {
        final SuspiciousPhoneTransfer existing = SuspiciousPhoneTransfer.builder()
                .id(PHONE_ID)
                .transferId(TRANSFER_ID)
                .blocked(false)
                .suspicious(true)
                .suspiciousReason(SUSPICIOUS_REASON)
                .build();
        when(phoneTransferRepository.findById(PHONE_ID)).thenReturn(Optional.of(existing));
        when(phoneTransferRepository.save(existing)).thenReturn(existing);

        final SuspiciousPhoneTransferDto result = transferService.updatePhone(PHONE_ID, phoneDto());

        assertThat(result.getId()).isEqualTo(PHONE_ID);
    }

    @Test
    @DisplayName("createAccount: сохраняет перевод по счёту")
    void createAccountShouldSaveTransfer() {
        when(accountTransferRepository.existsByTransferId(TRANSFER_ID)).thenReturn(false);
        when(accountTransferRepository.save(any(SuspiciousAccountTransfer.class))).thenAnswer(invocation -> {
            final SuspiciousAccountTransfer entity = invocation.getArgument(0);
            entity.setId(ACCOUNT_ID);
            return entity;
        });

        final SuspiciousAccountTransferDto result = transferService.createAccount(accountDto());

        assertThat(result.getId()).isEqualTo(ACCOUNT_ID);
    }

    @Test
    @DisplayName("getAccountById: возвращает перевод")
    void getAccountByIdShouldReturnTransfer() {
        when(accountTransferRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(
                SuspiciousAccountTransfer.builder()
                        .id(ACCOUNT_ID)
                        .transferId(TRANSFER_ID)
                        .blocked(false)
                        .suspicious(true)
                        .suspiciousReason(SUSPICIOUS_REASON)
                        .build()));

        final SuspiciousAccountTransferDto result = transferService.getAccountById(ACCOUNT_ID);

        assertThat(result.getId()).isEqualTo(ACCOUNT_ID);
    }

    @Test
    @DisplayName("deleteAccount: удаляет перевод")
    void deleteAccountShouldRemoveTransfer() {
        final SuspiciousAccountTransfer existing = SuspiciousAccountTransfer.builder()
                .id(ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .blocked(false)
                .suspicious(true)
                .suspiciousReason(SUSPICIOUS_REASON)
                .build();
        when(accountTransferRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(existing));

        transferService.deleteAccount(ACCOUNT_ID);

        verify(accountTransferRepository).delete(existing);
    }
}
