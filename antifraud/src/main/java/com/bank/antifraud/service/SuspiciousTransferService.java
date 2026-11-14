package com.bank.antifraud.service;

import com.bank.antifraud.dto.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.SuspiciousPhoneTransferDto;
import java.util.List;

public interface SuspiciousTransferService {

    SuspiciousCardTransferDto createCard(SuspiciousCardTransferDto transferDto);

    SuspiciousCardTransferDto getCardById(Long transferId);

    List<SuspiciousCardTransferDto> getAllCards();

    SuspiciousCardTransferDto updateCard(Long transferId, SuspiciousCardTransferDto transferDto);

    void deleteCard(Long transferId);

    SuspiciousPhoneTransferDto createPhone(SuspiciousPhoneTransferDto transferDto);

    SuspiciousPhoneTransferDto getPhoneById(Long transferId);

    List<SuspiciousPhoneTransferDto> getAllPhones();

    SuspiciousPhoneTransferDto updatePhone(Long transferId, SuspiciousPhoneTransferDto transferDto);

    void deletePhone(Long transferId);

    SuspiciousAccountTransferDto createAccount(SuspiciousAccountTransferDto transferDto);

    SuspiciousAccountTransferDto getAccountById(Long transferId);

    List<SuspiciousAccountTransferDto> getAllAccounts();

    SuspiciousAccountTransferDto updateAccount(Long transferId, SuspiciousAccountTransferDto transferDto);

    void deleteAccount(Long transferId);
}
