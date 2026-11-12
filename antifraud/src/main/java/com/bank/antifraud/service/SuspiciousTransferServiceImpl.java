package com.bank.antifraud.service;

import com.bank.antifraud.dto.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.SuspiciousPhoneTransferDto;
import com.bank.antifraud.entity.SuspiciousAccountTransfer;
import com.bank.antifraud.entity.SuspiciousCardTransfer;
import com.bank.antifraud.entity.SuspiciousPhoneTransfer;
import com.bank.antifraud.exception.DuplicateTransferException;
import com.bank.antifraud.exception.EntityNotFoundException;
import com.bank.antifraud.exception.TransferValidationException;
import com.bank.antifraud.mapper.SuspiciousTransferMapper;
import com.bank.antifraud.repository.SuspiciousAccountTransferRepository;
import com.bank.antifraud.repository.SuspiciousCardTransferRepository;
import com.bank.antifraud.repository.SuspiciousPhoneTransferRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SuspiciousTransferServiceImpl implements SuspiciousTransferService {

    private static final String CARD_NOT_FOUND = "Suspicious card transfer with id %d not found";
    private static final String PHONE_NOT_FOUND = "Suspicious phone transfer with id %d not found";
    private static final String ACCOUNT_NOT_FOUND = "Suspicious account transfer with id %d not found";
    private static final String CARD_EXISTS = "Suspicious card transfer with transferId %d already exists";
    private static final String PHONE_EXISTS = "Suspicious phone transfer with transferId %d already exists";
    private static final String ACCOUNT_EXISTS = "Suspicious account transfer with transferId %d already exists";
    private static final String SUSPICIOUS_REASON_REQUIRED = "suspiciousReason is required";
    private static final String BLOCKED_REASON_REQUIRED = "blockedReason is required for a blocked transfer";

    private final SuspiciousCardTransferRepository cardTransferRepository;
    private final SuspiciousPhoneTransferRepository phoneTransferRepository;
    private final SuspiciousAccountTransferRepository accountTransferRepository;
    private final SuspiciousTransferMapper mapper;

    @Override
    public SuspiciousCardTransferDto createCard(SuspiciousCardTransferDto transferDto) {
        validate(transferDto.getBlocked(), transferDto.getBlockedReason(), transferDto.getSuspiciousReason());
        if (cardTransferRepository.existsByTransferId(transferDto.getTransferId())) {
            throw new DuplicateTransferException(String.format(CARD_EXISTS, transferDto.getTransferId()));
        }
        final SuspiciousCardTransfer entity = mapper.toCardEntity(transferDto);
        return mapper.toCardDto(cardTransferRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public SuspiciousCardTransferDto getCardById(Long transferId) {
        return mapper.toCardDto(findCard(transferId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuspiciousCardTransferDto> getAllCards() {
        return mapper.toCardDtoList(cardTransferRepository.findAll());
    }

    @Override
    public SuspiciousCardTransferDto updateCard(Long transferId, SuspiciousCardTransferDto transferDto) {
        validate(transferDto.getBlocked(), transferDto.getBlockedReason(), transferDto.getSuspiciousReason());
        final SuspiciousCardTransfer entity = findCard(transferId);
        mapper.updateCard(transferDto, entity);
        return mapper.toCardDto(cardTransferRepository.save(entity));
    }

    @Override
    public void deleteCard(Long transferId) {
        cardTransferRepository.delete(findCard(transferId));
    }

    @Override
    public SuspiciousPhoneTransferDto createPhone(SuspiciousPhoneTransferDto transferDto) {
        validate(transferDto.getBlocked(), transferDto.getBlockedReason(), transferDto.getSuspiciousReason());
        if (phoneTransferRepository.existsByTransferId(transferDto.getTransferId())) {
            throw new DuplicateTransferException(String.format(PHONE_EXISTS, transferDto.getTransferId()));
        }
        final SuspiciousPhoneTransfer entity = mapper.toPhoneEntity(transferDto);
        return mapper.toPhoneDto(phoneTransferRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public SuspiciousPhoneTransferDto getPhoneById(Long transferId) {
        return mapper.toPhoneDto(findPhone(transferId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuspiciousPhoneTransferDto> getAllPhones() {
        return mapper.toPhoneDtoList(phoneTransferRepository.findAll());
    }

    @Override
    public SuspiciousPhoneTransferDto updatePhone(Long transferId, SuspiciousPhoneTransferDto transferDto) {
        validate(transferDto.getBlocked(), transferDto.getBlockedReason(), transferDto.getSuspiciousReason());
        final SuspiciousPhoneTransfer entity = findPhone(transferId);
        mapper.updatePhone(transferDto, entity);
        return mapper.toPhoneDto(phoneTransferRepository.save(entity));
    }

    @Override
    public void deletePhone(Long transferId) {
        phoneTransferRepository.delete(findPhone(transferId));
    }

    @Override
    public SuspiciousAccountTransferDto createAccount(SuspiciousAccountTransferDto transferDto) {
        validate(transferDto.getBlocked(), transferDto.getBlockedReason(), transferDto.getSuspiciousReason());
        if (accountTransferRepository.existsByTransferId(transferDto.getTransferId())) {
            throw new DuplicateTransferException(String.format(ACCOUNT_EXISTS, transferDto.getTransferId()));
        }
        final SuspiciousAccountTransfer entity = mapper.toAccountEntity(transferDto);
        return mapper.toAccountDto(accountTransferRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public SuspiciousAccountTransferDto getAccountById(Long transferId) {
        return mapper.toAccountDto(findAccount(transferId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuspiciousAccountTransferDto> getAllAccounts() {
        return mapper.toAccountDtoList(accountTransferRepository.findAll());
    }

    @Override
    public SuspiciousAccountTransferDto updateAccount(Long transferId, SuspiciousAccountTransferDto transferDto) {
        validate(transferDto.getBlocked(), transferDto.getBlockedReason(), transferDto.getSuspiciousReason());
        final SuspiciousAccountTransfer entity = findAccount(transferId);
        mapper.updateAccount(transferDto, entity);
        return mapper.toAccountDto(accountTransferRepository.save(entity));
    }

    @Override
    public void deleteAccount(Long transferId) {
        accountTransferRepository.delete(findAccount(transferId));
    }

    private SuspiciousCardTransfer findCard(Long transferId) {
        return cardTransferRepository.findById(transferId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(CARD_NOT_FOUND, transferId)));
    }

    private SuspiciousPhoneTransfer findPhone(Long transferId) {
        return phoneTransferRepository.findById(transferId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(PHONE_NOT_FOUND, transferId)));
    }

    private SuspiciousAccountTransfer findAccount(Long transferId) {
        return accountTransferRepository.findById(transferId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ACCOUNT_NOT_FOUND, transferId)));
    }

    private void validate(Boolean blocked, String blockedReason, String suspiciousReason) {
        if (suspiciousReason == null || suspiciousReason.isBlank()) {
            throw new TransferValidationException(SUSPICIOUS_REASON_REQUIRED);
        }
        if (Boolean.TRUE.equals(blocked) && (blockedReason == null || blockedReason.isBlank())) {
            throw new TransferValidationException(BLOCKED_REASON_REQUIRED);
        }
    }
}
