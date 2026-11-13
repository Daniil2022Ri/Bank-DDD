package com.bank.transfer.service;

import com.bank.transfer.dto.AccountTransferDto;
import com.bank.transfer.dto.CardTransferDto;
import com.bank.transfer.dto.PhoneTransferDto;
import com.bank.transfer.entity.AccountTransfer;
import com.bank.transfer.entity.CardTransfer;
import com.bank.transfer.entity.PhoneTransfer;
import com.bank.transfer.exception.DuplicateTransferException;
import com.bank.transfer.exception.EntityNotFoundException;
import com.bank.transfer.mapper.TransferMapper;
import com.bank.transfer.repository.AccountTransferRepository;
import com.bank.transfer.repository.CardTransferRepository;
import com.bank.transfer.repository.PhoneTransferRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TransferServiceImpl implements TransferService {

    private static final String ACCOUNT_TRANSFER_NOT_FOUND = "Account transfer with id %d not found";
    private static final String CARD_TRANSFER_NOT_FOUND = "Card transfer with id %d not found";
    private static final String PHONE_TRANSFER_NOT_FOUND = "Phone transfer with id %d not found";
    private static final String ACCOUNT_NUMBER_EXISTS = "Transfer with account number %d already exists";
    private static final String CARD_NUMBER_EXISTS = "Transfer with card number %d already exists";

    private final AccountTransferRepository accountTransferRepository;
    private final CardTransferRepository cardTransferRepository;
    private final PhoneTransferRepository phoneTransferRepository;
    private final TransferMapper transferMapper;

    @Override
    public AccountTransferDto createAccount(AccountTransferDto transferDto) {
        if (accountTransferRepository.existsByAccountNumber(transferDto.getAccountNumber())) {
            throw new DuplicateTransferException(String.format(ACCOUNT_NUMBER_EXISTS,
                    transferDto.getAccountNumber()));
        }
        final AccountTransfer transfer = transferMapper.toAccountEntity(transferDto);
        return transferMapper.toAccountDto(accountTransferRepository.save(transfer));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountTransferDto getAccountById(Long transferId) {
        return transferMapper.toAccountDto(findAccount(transferId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountTransferDto> getAllAccounts() {
        return transferMapper.toAccountDtoList(accountTransferRepository.findAll());
    }

    @Override
    public AccountTransferDto updateAccount(Long transferId, AccountTransferDto transferDto) {
        final AccountTransfer transfer = findAccount(transferId);
        transferMapper.updateAccount(transferDto, transfer);
        return transferMapper.toAccountDto(accountTransferRepository.save(transfer));
    }

    @Override
    public void deleteAccount(Long transferId) {
        accountTransferRepository.delete(findAccount(transferId));
    }

    @Override
    public CardTransferDto createCard(CardTransferDto transferDto) {
        if (cardTransferRepository.existsByCardNumber(transferDto.getCardNumber())) {
            throw new DuplicateTransferException(String.format(CARD_NUMBER_EXISTS,
                    transferDto.getCardNumber()));
        }
        final CardTransfer transfer = transferMapper.toCardEntity(transferDto);
        return transferMapper.toCardDto(cardTransferRepository.save(transfer));
    }

    @Override
    @Transactional(readOnly = true)
    public CardTransferDto getCardById(Long transferId) {
        return transferMapper.toCardDto(findCard(transferId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardTransferDto> getAllCards() {
        return transferMapper.toCardDtoList(cardTransferRepository.findAll());
    }

    @Override
    public CardTransferDto updateCard(Long transferId, CardTransferDto transferDto) {
        final CardTransfer transfer = findCard(transferId);
        transferMapper.updateCard(transferDto, transfer);
        return transferMapper.toCardDto(cardTransferRepository.save(transfer));
    }

    @Override
    public void deleteCard(Long transferId) {
        cardTransferRepository.delete(findCard(transferId));
    }

    @Override
    public PhoneTransferDto createPhone(PhoneTransferDto transferDto) {
        final PhoneTransfer transfer = transferMapper.toPhoneEntity(transferDto);
        return transferMapper.toPhoneDto(phoneTransferRepository.save(transfer));
    }

    @Override
    @Transactional(readOnly = true)
    public PhoneTransferDto getPhoneById(Long transferId) {
        return transferMapper.toPhoneDto(findPhone(transferId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhoneTransferDto> getAllPhones() {
        return transferMapper.toPhoneDtoList(phoneTransferRepository.findAll());
    }

    @Override
    public PhoneTransferDto updatePhone(Long transferId, PhoneTransferDto transferDto) {
        final PhoneTransfer transfer = findPhone(transferId);
        transferMapper.updatePhone(transferDto, transfer);
        return transferMapper.toPhoneDto(phoneTransferRepository.save(transfer));
    }

    @Override
    public void deletePhone(Long transferId) {
        phoneTransferRepository.delete(findPhone(transferId));
    }

    private AccountTransfer findAccount(Long transferId) {
        return accountTransferRepository.findById(transferId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ACCOUNT_TRANSFER_NOT_FOUND, transferId)));
    }

    private CardTransfer findCard(Long transferId) {
        return cardTransferRepository.findById(transferId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(CARD_TRANSFER_NOT_FOUND, transferId)));
    }

    private PhoneTransfer findPhone(Long transferId) {
        return phoneTransferRepository.findById(transferId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(PHONE_TRANSFER_NOT_FOUND, transferId)));
    }
}
