package com.bank.transfer.service;

import com.bank.transfer.dto.AccountTransferDto;
import com.bank.transfer.dto.CardTransferDto;
import com.bank.transfer.dto.PhoneTransferDto;
import java.util.List;

public interface TransferService {

    AccountTransferDto createAccount(AccountTransferDto transferDto);

    AccountTransferDto getAccountById(Long transferId);

    List<AccountTransferDto> getAllAccounts();

    AccountTransferDto updateAccount(Long transferId, AccountTransferDto transferDto);

    void deleteAccount(Long transferId);

    CardTransferDto createCard(CardTransferDto transferDto);

    CardTransferDto getCardById(Long transferId);

    List<CardTransferDto> getAllCards();

    CardTransferDto updateCard(Long transferId, CardTransferDto transferDto);

    void deleteCard(Long transferId);

    PhoneTransferDto createPhone(PhoneTransferDto transferDto);

    PhoneTransferDto getPhoneById(Long transferId);

    List<PhoneTransferDto> getAllPhones();

    PhoneTransferDto updatePhone(Long transferId, PhoneTransferDto transferDto);

    void deletePhone(Long transferId);
}
