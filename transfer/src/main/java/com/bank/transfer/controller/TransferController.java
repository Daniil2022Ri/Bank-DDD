package com.bank.transfer.controller;

import com.bank.transfer.dto.AccountTransferDto;
import com.bank.transfer.dto.CardTransferDto;
import com.bank.transfer.dto.PhoneTransferDto;
import com.bank.transfer.service.TransferService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Transfers API", description = "Переводы по счёту, карте и номеру телефона")
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/account")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountTransferDto createAccount(@Valid @RequestBody AccountTransferDto transferDto) {
        return transferService.createAccount(transferDto);
    }

    @GetMapping("/account")
    public List<AccountTransferDto> getAllAccounts() {
        return transferService.getAllAccounts();
    }

    @GetMapping("/account/{transferId}")
    public AccountTransferDto getAccountById(@PathVariable Long transferId) {
        return transferService.getAccountById(transferId);
    }

    @PutMapping("/account/{transferId}")
    public AccountTransferDto updateAccount(@PathVariable Long transferId,
                                            @Valid @RequestBody AccountTransferDto transferDto) {
        return transferService.updateAccount(transferId, transferDto);
    }

    @DeleteMapping("/account/{transferId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long transferId) {
        transferService.deleteAccount(transferId);
    }

    @PostMapping("/card")
    @ResponseStatus(HttpStatus.CREATED)
    public CardTransferDto createCard(@Valid @RequestBody CardTransferDto transferDto) {
        return transferService.createCard(transferDto);
    }

    @GetMapping("/card")
    public List<CardTransferDto> getAllCards() {
        return transferService.getAllCards();
    }

    @GetMapping("/card/{transferId}")
    public CardTransferDto getCardById(@PathVariable Long transferId) {
        return transferService.getCardById(transferId);
    }

    @PutMapping("/card/{transferId}")
    public CardTransferDto updateCard(@PathVariable Long transferId,
                                      @Valid @RequestBody CardTransferDto transferDto) {
        return transferService.updateCard(transferId, transferDto);
    }

    @DeleteMapping("/card/{transferId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable Long transferId) {
        transferService.deleteCard(transferId);
    }

    @PostMapping("/phone")
    @ResponseStatus(HttpStatus.CREATED)
    public PhoneTransferDto createPhone(@Valid @RequestBody PhoneTransferDto transferDto) {
        return transferService.createPhone(transferDto);
    }

    @GetMapping("/phone")
    public List<PhoneTransferDto> getAllPhones() {
        return transferService.getAllPhones();
    }

    @GetMapping("/phone/{transferId}")
    public PhoneTransferDto getPhoneById(@PathVariable Long transferId) {
        return transferService.getPhoneById(transferId);
    }

    @PutMapping("/phone/{transferId}")
    public PhoneTransferDto updatePhone(@PathVariable Long transferId,
                                        @Valid @RequestBody PhoneTransferDto transferDto) {
        return transferService.updatePhone(transferId, transferDto);
    }

    @DeleteMapping("/phone/{transferId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePhone(@PathVariable Long transferId) {
        transferService.deletePhone(transferId);
    }
}
